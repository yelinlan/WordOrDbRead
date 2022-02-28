package com.yelinlan.wordread;

import java.util.List;

/**
 *<ul>
 *<li>类名称: DocTable</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/24 16:42</li>
 *</ul>
 **/
public class DocTable {
	private String tablename;
	private String tableComment;
	private List<TableField> fields;

	public DocTable() {
	}

	public DocTable(String tablename, String tableComment) {
		this.tablename = tablename;
		this.tableComment = tableComment;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getTableComment() {
		return tableComment;
	}

	public void setTableComment(String tableComment) {
		this.tableComment = tableComment;
	}

	public List<TableField> getFields() {
		return fields;
	}

	public void setFields(List<TableField> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "DocTable{" + "tablename='" + tablename + '\'' + ", tableComment='" + tableComment + '\'' + ", fields="
				+ fields + '}';
	}
}