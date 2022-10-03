package com.edu.ulab.app.mapper;

import com.edu.ulab.app.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {

        Person user = new Person();

        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setTitle(rs.getString("title"));
        user.setAge(rs.getInt("age"));
        user.setBooks(new ArrayList<>());

        return user;
    }
}
