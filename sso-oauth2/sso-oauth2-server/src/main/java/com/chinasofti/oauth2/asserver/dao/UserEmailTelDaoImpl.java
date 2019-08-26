package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.User;
import com.chinasofti.oauth2.asserver.entity.UserEmailTel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by makai on 2016/1/21 0021.
 */
@Repository
public class UserEmailTelDaoImpl implements UserEmailTelDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public UserEmailTel findOne(String userId) {
        String sql = "SELECT id AS userId ,telphone AS tel,email AS email FROM t_user WHERE id = ?";
        List<UserEmailTel> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(UserEmailTel.class), userId);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public void update(String userId, String email, String tel) {
        String sql = "UPDATE t_user SET email = ? ,telphone = ? WHERE id = ?";
        jdbcTemplate.update(sql,email,tel,userId);
    }

    /**
     * 根据用户id,获取其详细信息. 包括登录名和真实姓名
     *
     * @param userId
     * @return
     */
    @Override
    public UserEmailTel fingAccount(String userId) {
        String sql = "SELECT u.id AS userId,u.telphone AS tel,u.email AS email,u.username AS username,up.account AS account FROM t_user u,t_user_pass up WHERE u.id = up.user_id AND u.id = ?";
        List<UserEmailTel> userList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(UserEmailTel.class), userId);
        if(userList.size() == 0) {
            return null;
        }
        return userList.get(0);
    }
}
