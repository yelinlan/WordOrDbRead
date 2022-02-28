package com.yelinlan.wordread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yelinlan.wordread.DbUtils.getJdbcTemplate;


/**
 *<ul>
 *<li>类名称: Test</li>
 *<li>类描述: 用于读取word文档数据</li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/8/5 10:56</li>
 *</ul>
 **/
public class WordTableUtils {
	private static String level = "3";//三级标题
	private static List<XWPFTableCell> cells;//docx表行
	private static List<DocTable> tables = new ArrayList();

	public static List<XWPFTableCell> getCells() {
		return cells;
	}

	public static void setCells(List<XWPFTableCell> cells) {
		WordTableUtils.cells = cells;
	}

	public static List<DocTable> getTables() {
		return tables;
	}

	public static void setTables(List<DocTable> tables) {
		WordTableUtils.tables = tables;
	}

	public static List<String> getSqlList() {
		return sqlList;
	}

	public static void setSqlList(List<String> sqlList) {
		WordTableUtils.sqlList = sqlList;
	}

	private static List<String> sqlList = Lists.newArrayList();//建表sql集合


	/*  注意事项：
		获取三级标题  xxxxx(xxxxxx)
		表格五列  序号0	字段名1	意义2	说明3	字段类型4
		表名与表格一一对应
	*/
	public static List<DocTable> readDocx(String fileName) {
		try {
			InputStream inputStream = new FileInputStream(fileName);
			XWPFDocument xwpfDocument = new XWPFDocument(inputStream);
			//获取level级标题
			List<String> tablenames = xwpfDocument.getParagraphs().stream().filter(p -> level.equals(p.getStyleID()))
					.map(XWPFParagraph::getParagraphText).collect(Collectors.toList());
			//表格
			List<XWPFTable> tableList = xwpfDocument.getTables();
			if (tableList.size() != tablenames.size()) {
				throw new RuntimeException("表标题与表格个数不匹配");
			}
			for (int j = 0; j < tableList.size(); j++) {
				//通过标题拿到当前表名和注释
				DocTable docTable = getDocTableByNameAndComment(tablenames.get(j));
				//每一行的List
				List<XWPFTableRow> rows = tableList.get(j).getRows();
				//读取每一行数据（除去第一行表头 ）
				List<TableField> fields = new ArrayList<>();
				for (int i = 1; i < rows.size(); i++) {
					cells = rows.get(i).getTableCells();
					String comment = cell(2) + cell(3);
					String type = Convert.toStr(ReUtil.get("\\w+(?=\\s*[\\(\\（]*)", cell(4), 0), "").toUpperCase();
					String key = cell(1).toUpperCase();
					String tableName = docTable.getTablename().toUpperCase();
					String len = Convert.toStr(ReUtil.get("\\d+(?=\\s*[\\）\\)]+)", cell(4), 0), "").toUpperCase();
					String allType = cell(4);
					fields.add(new TableField(key, type, len, comment, tableName, allType));
				}
				docTable.setFields(fields);
				tables.add(docTable);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tables;
	}

	private static String cell(int i) {
		if (cells.size()<=i){
			return "";
		}
		return cells.get(i).getText().trim();
	}

	//xxxxx(xxxxxx)
	private static DocTable getDocTableByNameAndComment(String pt) {
		String tablename = null;
		String tableComment = null;
		String s = pt.replace("（", "(").replace("）", ")");
		if (s.contains("(")) {
			tablename = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
			tableComment = s.substring(0, s.indexOf("("));
			return new DocTable(tablename, tableComment);
		}
		return new DocTable(tablename, tableComment);
	}

	private static String getLevel() {
		return level;
	}

	private static void setLevel(String level) {
		WordTableUtils.level = level;
	}

	//CREATE TABLE ` xxxxx ` ( ` fcid ` INT PRIMARY KEY COMMENT '编号主键， xxx主键。',
	//` classid ` INT COMMENT 'xx编码' ) comment = 'xxxxx表';
	public static List<String> genMySql() {
		sqlList.clear();//mysql
		for (DocTable table : tables) {
			StringBuilder sql = new StringBuilder();
			sql.append(getHeader(table));
			sql.append(StrUtil.format("create table `{}` ( \n", table.getTablename()));
			//读取每一行数据（除去第一行表头 ）
			int size = table.getFields().size();
			for (int i = 0; i < size; i++) {
				TableField field = table.getFields().get(i);
				if (i == 0) {
					sql.append(StrUtil.format("	`{}` {} primary key COMMENT '{}',\n", field.getKey(),
							field.getAllType(), field.getComment()));
				} else {
					sql.append(StrUtil.format("	`{}` {} COMMENT '{}'", field.getKey(), field.getAllType(),
							field.getComment()));
					if (size - 1 != i) {
						sql.append(",\n");
					}
				}
			}
			sql.append(StrUtil.format("\n)comment='{}';\n", table.getTableComment()));
			sqlList.add(sql.toString());
		}
		return sqlList;
	}

	private static String getHeader(DocTable table) {
//		return StrUtil.format(">>>>>>>>>>>>>>>>>>>>>{}（{}）\n", table.getTableComment(), table.getTablename());
		return "";
	}


	//CREATE TABLE [xxxxx] (	[AttentionId] INT PRIMARY KEY,	[ActId] INT;
	//EXECUTE sp_addextendedproperty 'MS_Description',	'xx编号主键',	'user',	'dbo',	'table',	'xxxx',	'column',	'AttentionId';
	//EXECUTE sp_addextendedproperty 'MS_Description',	'xxxid',	'user',	'dbo',	'table',	'xxxx',	'column',	'ActId';
	//EXECUTE sp_addextendedproperty 'MS_Description',	'xxx表',	'user',	'dbo',	'table',	'xxxx',NULL,NULL;
	public static List<String> genSqlserver() {
		sqlList.clear();//sqlserver
		for (DocTable table : tables) {
			StringBuilder sql = new StringBuilder();
			sql.append(getHeader(table));
			sql.append(StrUtil.format("create table [dbo].[{}] ( \n", table.getTablename()));
			StringBuilder memo = new StringBuilder();
			//读取每一行数据（除去第一行表头 ）
			int size = table.getFields().size();
			for (int i = 0; i < size; i++) {
				TableField field = table.getFields().get(i);
				if (i == 0) {
					sql.append(StrUtil.format("	[{}] {} identity not null primary key,\n", field.getKey(), field.getAllType()));
				} else {
					sql.append(StrUtil.format("	[{}] {}", field.getKey(), field.getAllType()));
					if (size - 1 != i) {
						sql.append(",\n");
					}
				}
				memo.append(StrUtil.format(
						"execute sp_addextendedproperty    'MS_Description','{}',  'user','dbo','table','{}','column','{}';\n",
						field.getComment(), field.getTableName(), field.getKey()));
			}
			sql.append("\n);\n");
			memo.append(StrUtil.format(
					"execute sp_addextendedproperty    'MS_Description','{}',  'user','dbo','table','{}',null,null;\n",
					table.getTableComment(), table.getTablename()));
			sql.append(memo);
			sqlList.add(sql.toString());
		}
		return sqlList;
	}

	public static List<String> genOracle() {
		sqlList.clear();//oracle
		for (DocTable table : tables) {
			StringBuilder sql = new StringBuilder();
			sql.append(getHeader(table));
			sql.append("create table \"" + table.getTablename() + "\" ( \n");
			StringBuilder memo = new StringBuilder();
			//读取每一行数据（除去第一行表头 ）
			int size = table.getFields().size();
			for (int i = 0; i < size; i++) {
				TableField field = table.getFields().get(i);
				if (i == 0) {
					sql.append(StrUtil.format("	\"{}\" {} primary key,\n", field.getKey(), field.getAllType()));
				} else {
					sql.append(StrUtil.format("	\"{}\" {}", field.getKey(), field.getAllType()));
					if (size - 1 != i) {
						sql.append(",\n");
					}
				}
				memo.append(StrUtil.format("comment  on column {}.{} is '{}';\n", field.getTableName(), field.getKey(),
						field.getComment()));
			}
			sql.append("\n);\n");
			memo.append(
					StrUtil.format("comment  on table {} is '{}';\n", table.getTablename(), table.getTableComment()));
			sql.append(memo);
			sqlList.add(sql.toString());
		}
		return sqlList;
	}

	public static void writeFile(String... path) {
		String filepath = path != null && path.length != 0 ? path[0] : "E:\\exportSql.txt";
		FileUtil.writeLines(sqlList, filepath, StandardCharsets.UTF_8);
	}

	public static void setDataBaseTable() {
		String columnDetail =
				" SELECT  d.name as tableName ,a.name as [key], b.name as type, COLUMNPROPERTY(a.id,a.name,'PRECISION')  as len,isnull(g.[value], ' ') AS comment, "
						+" ( CASE WHEN ( SELECT COUNT ( * ) FROM sysobjects WHERE ( name IN ( SELECT name FROM sysindexes WHERE ( id = a.id ) AND ( indid IN ( SELECT indid FROM sysindexkeys WHERE ( id = a.id ) AND ( colid IN ( SELECT colid FROM syscolumns WHERE ( id = a.id ) AND ( name = a.name ))))))) AND ( xtype = 'PK' )) > 0 THEN 'pk' ELSE '' END ) pk"
						+ " FROM  syscolumns a  left join systypes b on a.xtype=b.xusertype   inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties'  "
						+ " left join syscomments e on a.cdefault=e.id   left join sys.extended_properties g on a.id=g.major_id AND a.colid=g.minor_id "
						+ " left join sys.extended_properties f on d.id=f.class and f.minor_id=0  where b.name is not null  order by a.id,a.colorder ";
		List<Map<String, Object>> query1 = getJdbcTemplate().queryForList(columnDetail);
		if (CollectionUtil.isNotEmpty(query1)) {
			Map<String, List<Map<String, String>>> rts = query1.stream().map(p -> {
				Map<String, String> rt = Maps.newHashMap();
				for (Map.Entry<String, Object> entry : p.entrySet()) {
					if ("tableName".equals(entry.getKey()) || "tableKey".equals(entry.getKey()) || "keyType".equals(
							entry.getKey())) {
						rt.put(entry.getKey(), Convert.toStr(entry.getValue(), "").toUpperCase());
					} else {
						rt.put(entry.getKey(), Convert.toStr(entry.getValue(), ""));
					}
				}
				return rt;
			}).collect(Collectors.groupingBy(p -> p.get("tableName")));

			for (Map.Entry<String, List<Map<String, String>>> entry : rts.entrySet()) {
				DocTable docTable = new DocTable();
				docTable.setTablename(entry.getKey());
				List<TableField> fields = new ArrayList<>();
				entry.getValue().forEach(p -> {
					String comment = Convert.toStr(p.get("comment"), "");
					String type = Convert.toStr(p.get("type"), "").toUpperCase();
					String key = Convert.toStr(p.get("key"), "");
					String tableName = Convert.toStr(p.get("tableName"), "");
					String len = Convert.toStr(p.get("len"), "");
					String allType = type + "(" + len + ")";
					String pk = Convert.toStr(p.get("pk"), "");
					fields.add(new TableField(key, type, len, comment, tableName, allType,pk));
				});
				docTable.setFields(fields);
				tables.add(docTable);
			}
		}
	}

	private static class Maps {
		public static Map<String, String> newHashMap() {
			return new HashMap<>();
		}
	}
}
