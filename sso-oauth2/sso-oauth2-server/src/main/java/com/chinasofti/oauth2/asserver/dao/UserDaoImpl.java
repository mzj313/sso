package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public User createUser(final User user) {
        final String sql = "insert into t_user_pass(id, user_id, password, account, createtime, salt) values(?,?,?,?,?,?)";

        jdbcTemplate.update(
                sql,
                user.getId(),user.getUserId(),user.getPassword(),user.getUsername(),user.getCreateTime(),user.getSalt());
        return user;
    }

    public User updateUser(User user) {
        String sql = "update t_user_pass set account=?, password=?, salt=? where user_id=?";
        jdbcTemplate.update(
                sql,
                user.getUsername(),user.getPassword(),user.getSalt(),user.getUserId());
        return user;
    }

    public void deleteUser(String userId) {
        String sql = "delete from t_user_pass where user_id=?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public User findOne(String userId) {
        String sql = "select id, user_id, account as username, password, createtime, salt from t_user_pass where user_id=?";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), userId);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<User> findAll() {
        String sql = "select id, user_id, account as username, password, createtime, salt from t_user_pass";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class));
    }


    @Override
    public User findByUsername(String username) {
        String sql = "select id, user_id, account as username, password, createtime, salt from t_user_pass up where account=?";
        sql += " or exists(select 1 from t_user u where u.id=up.user_id and (u.telphone=? or u.email=?))";
        List<User> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(User.class), username,username,username);
        if(userList.size() == 0) {
            return null;
        }
        if(userList.size() > 1) {
            throw new RuntimeException("登录账号不唯一");
        }
        return userList.get(0);
    }
}
