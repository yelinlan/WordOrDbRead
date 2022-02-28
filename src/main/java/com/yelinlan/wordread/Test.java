package com.yelinlan.wordread;

/**
 *<ul>
 *<li>类名称: Test</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/24 17:20</li>
 *</ul>
 **/
public class Test {
	public static void main(String[] args) {
		WordTableUtils.setDataBaseTable();
		WordTableUtils.genSqlserver();
		WordTableUtils.writeFile("E:\\my1.txt");
		WordTableUtils.setDataBaseTable();
	}
}