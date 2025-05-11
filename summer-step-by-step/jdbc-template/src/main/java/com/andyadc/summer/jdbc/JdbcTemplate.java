package com.andyadc.summer.jdbc;

import javax.sql.DataSource;

public class JdbcTemplate {

    final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
