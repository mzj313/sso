package com.chinasofti.oauth2.asserver.entity;

import org.apache.shiro.subject.PrincipalCollection;

import java.io.Serializable;
import java.util.Date;

/**TODO 记录登录cookie验证对象
 * Created by yangkai on 15/5/4.
 */
public class PersistentRememberMeToken implements Serializable{

    private String username;
    private String series;
    private String tokenValue;
    private Date date;

    private PrincipalCollection principalCollection;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PrincipalCollection getPrincipalCollection() {
        return principalCollection;
    }

    public void setPrincipalCollection(PrincipalCollection principalCollection) {
        this.principalCollection = principalCollection;
    }
}
