package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.UserEmailTel;

/**
 * Created by makai on 2016/1/21 0021.
 */
public interface UserEmailTelService {
    UserEmailTel fingOne(String userId);
    String update(String userId,String email,String tel,String code,String emailCode);

    /**
     * 获取用户信息 包含登录名称 以及 真实姓名
     * @param userId
     * @return
     */
    UserEmailTel findAccout(String userId);

    /**
     * 发送手机验证码
     * @param userId
     * @param tel
     * @return
     */
    String sendCode(String userId,String tel);

    /**
     * 发送邮箱验证码
     * @param userId
     * @param email
     * @return
     */
    public String sendEmailCode(String userId,String email);

}
