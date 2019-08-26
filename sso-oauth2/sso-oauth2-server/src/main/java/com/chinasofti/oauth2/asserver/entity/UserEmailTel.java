package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;

/**
 * 用户 email 和 tel
 * Created by makai on 2016/1/21 0021.
 */
public class UserEmailTel implements Serializable{
    private String userId;
    private String email;
    private String tel;
    private String account; //登录帐号
    private String username; //用户真实姓名

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
