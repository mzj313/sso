package com.ssyt.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ssyt.auth.domain.UserLogin;

public class UserDaoImpl implements UserDao{
	private  JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public UserLogin getUserByUsername(String username) {
		String sql = "select * from users where username='" + username + "' limit 1";
		RowMapper<UserLogin > rm = BeanPropertyRowMapper.newInstance(UserLogin.class);
		UserLogin userLogin = jdbcTemplate.queryForObject(sql, rm);
		return userLogin;
	}

	
}
