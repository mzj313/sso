package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;

/**
 * Created by yangkai on 15/6/15.
 */
public class PhoneAuth implements Serializable{
    String phoneId;
    String username;
    String password;
    boolean rememberMe;

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
