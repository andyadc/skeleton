package com.andyadc.summer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {

    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
