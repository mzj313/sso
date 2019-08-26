package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;

/**
 * Created by KK on 2015/5/19.
 */
public class TUser implements Serializable{
    private String openId;
    private String userId;
    private String username;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
