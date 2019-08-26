package com.ssyt.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ssyt.auth.domain.UserLogin;

public interface UserDao {

	UserLogin  getUserByUsername(String username);
	
	void  setJdbcTemplate(JdbcTemplate jdbcTemplate);
}
