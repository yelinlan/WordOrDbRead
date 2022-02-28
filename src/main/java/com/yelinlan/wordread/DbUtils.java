package com.yelinlan.wordread;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *<ul>
 *<li>类名称: DbUtils</li>
 *<li>类描述: </li>
 *<li>创建人: quanyixiang</li>
 *<li>创建时间: 2021/12/27 9:34</li>
 *</ul>
 **/
public class DbUtils {
	private static JdbcTemplate jdbcTemplate;

	static {
		String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String url = "";
		String username = "";
		String password = "";
		DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
		dataSource.setDriverClassName(driverClassName);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public static JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}