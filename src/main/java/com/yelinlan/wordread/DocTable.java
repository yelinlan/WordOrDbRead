package com.yelinlan.wordread;

import lombok.Data;

import java.util.List;

/**
 *<ul>
 *<li>类名称: DocTable</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/24 16:42</li>
 *</ul>
 **/
@Data
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
}