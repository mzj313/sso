package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.User;

import java.util.List;

/**
 */
public interface UserDao {

    public User createUser(User user);
    public User updateUser(User user);
    public void deleteUser(String userId);

    User findOne(String userId);

    List<User> findAll();

    User findByUsername(String username);

}
