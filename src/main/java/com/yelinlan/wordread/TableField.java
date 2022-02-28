package com.yelinlan.wordread;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *<ul>
 *<li>类名称: TableField</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/24 16:44</li>
 *</ul>
 **/
@Data //@code @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
@AllArgsConstructor
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

}