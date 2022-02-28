package com.yelinlan.wordread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *<ul>
 *<li>类名称: FindNvarchr</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2022/2/22 16:59</li>
 *</ul>
 **/
public class FindNvarchr {
	static List<String> pks;
	public static void main(String[] args) {

		List<String> tablenames = FileUtil
				.readLines("C:\\Users\\quanyixiang\\Desktop\\alltables.txt", StandardCharsets.UTF_8);
		tablenames = tablenames.stream().map(String::toUpperCase).collect(Collectors.toList());

		WordTableUtils.setDataBaseTable();
		List<DocTable> tables = WordTableUtils.getTables();
		List<String> finalTablenames = tablenames;
		tables = tables.stream().filter(p-> finalTablenames.contains(p.getTablename().toUpperCase())).collect(Collectors.toList());
		List<DocTable> list = tables.stream().peek(p -> p.setFields(
				p.getFields().stream().filter(q -> q.getType().equalsIgnoreCase("VARCHAR")).filter(q-> Convert.toInt(q.getLen())!=-1)
						.collect(Collectors.toList()))).collect(Collectors.toList());

		List<String> alterLength = new ArrayList<>();
		pks = new ArrayList<>();
		list.forEach(p -> {
			List<String> primarykeys = p.getFields().stream().filter(q -> q.getPk().equalsIgnoreCase("pk"))
					.map(TableField::getKey).collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(primarykeys)) {
				pks.addAll(primarykeys);
			}
		});

		list.forEach(p-> alterLength.addAll(alterTableFieldsLength(p)));
		alterLength.forEach(System.out::println);



		/*getAllTablenames*/
		/*WordTableUtils.readDocx("E:\\产品线\\2203版\\数据结构设计说明书 - 副本.docx");
		List<DocTable> tables = WordTableUtils.getTables();
		List<String> list = tables.stream().map(DocTable::getTablename).collect(Collectors.toList());
		list.forEach(System.out::println);*/
	}

	public static List<String> alterTableFieldsLength(DocTable docTable) {
		return docTable.getFields().stream().map(p -> {
			if (!pks.contains(p.getKey())) {
				return StrUtil.format(
						"IF NOT EXISTS ( SELECT TOP 1  1 FROM    INFORMATION_SCHEMA.COLUMNS WHERE   [TABLE_NAME] = \'{}\'  AND [COLUMN_NAME] = \'{}\' )\n "
								+ "ALTER TABLE [dbo].[{}] ALTER COLUMN [{}] VARCHAR({})\n"
								+ "GO\n", p.getTableName(),p.getKey(),p.getTableName(),
						p.getKey(), Convert.toInt(p.getLen()));
			} else {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static List<String> originTableFieldsLength(DocTable docTable) {
		return docTable.getFields().stream().map(p -> {
			if (!pks.contains(p.getKey())) {
				return StrUtil.format("ALTER TABLE [dbo].[{}] ALTER COLUMN [{}] VARCHAR({})", p.getTableName(),
						p.getKey(), Convert.toInt(p.getLen()));
			} else {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
}