package com.yelinlan.wordread;

/**
 *<ul>
 *<li>类名称: TableField</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/24 16:44</li>
 *</ul>
 **/
public class TableField {

	private String key;
	private String type;
	private String len;
	private String comment;
	private String tableName;
	private String allType;
	private String pk;

	public TableField(String key, String type, String len, String comment, String tableName,String allType) {
		this.key = key;
		this.type = type;
		this.len = len;
		this.comment = comment;
		this.tableName = tableName;
		this.allType = allType;
	}

	public TableField(String key, String type, String len, String comment, String tableName, String allType,
			String pk) {
		this.key = key;
		this.type = type;
		this.len = len;
		this.comment = comment;
		this.tableName = tableName;
		this.allType = allType;
		this.pk = pk;
	}

	public String getAllType() {
		return allType;
	}

	public void setAllType(String allType) {
		this.allType = allType;
	}

	public TableField() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLen() {
		return len;
	}

	public void setLen(String len) {
		this.len = len;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPk() {
		return pk;
	}

	public void setPk(String pk) {
		this.pk = pk;
	}

	@Override
	public String toString() {
		return "TableField{" + "key='" + key + '\'' + ", type='" + type + '\'' + ", len='" + len + '\'' + ", comment='"
				+ comment + '\'' + ", tableName='" + tableName + '\'' + '}';
	}
}