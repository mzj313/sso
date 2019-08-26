package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.UserEmailTel;

/**
 * Created by makai on 2016/1/21 0021.
 */
public interface UserEmailTelDao {
    UserEmailTel findOne(String userId);
    void update(String userId,String email,String tel);

    /**
     * 根据用户id,获取其详细信息. 包括登录名和真实姓名
     * @param userId
     * @return
     */
    UserEmailTel fingAccount(String userId);
}
