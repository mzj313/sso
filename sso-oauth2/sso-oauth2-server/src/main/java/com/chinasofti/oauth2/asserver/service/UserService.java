package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.User;

import java.util.List;

/**
 */
public interface UserService {
    /**
     * 创建用户
     * @param user
     */
    public User createUser(User user);

    public User updateUser(User user);

    public void deleteUser(String userId);

    /**
     * 修改密码
     * @param userId
     * @param newPassword
     */
    public void changePassword(String userId, String newPassword);

    User findOne(String userId);

    List<User> findAll();

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username);

}
