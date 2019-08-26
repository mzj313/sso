package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class Client implements Serializable {

    private String id;
    private String clientId;
    private String clientSecret;
    private String name;
    private String url;
    private String displayOrder;
    private String comment;
    private String accessGroupId;
    private String secretKeyPrivate;
    private String secretKeyPublic;
    private Date createtime;
    private String code;
    private String scope;
    private Integer accessTokenValidity;
    private Integer refreshTokenValidity;

    //新增字段
    private String appType;
    private String loginFilePath;

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getLoginFilePath() {
        return loginFilePath;
    }

    public void setLoginFilePath(String loginFilePath) {
        this.loginFilePath = loginFilePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(String accessGroupId) {
        this.accessGroupId = accessGroupId;
    }

    public String getSecretKeyPrivate() {
        return secretKeyPrivate;
    }

    public void setSecretKeyPrivate(String secretKeyPrivate) {
        this.secretKeyPrivate = secretKeyPrivate;
    }

    public String getSecretKeyPublic() {
        return secretKeyPublic;
    }

    public void setSecretKeyPublic(String secretKeyPublic) {
        this.secretKeyPublic = secretKeyPublic;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (id != null ? !id.equals(client.id) : client.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", displayOrder='" + displayOrder + '\'' +
                ", comment='" + comment + '\'' +
                ", accessGroupId='" + accessGroupId + '\'' +
                ", secretKeyPrivate='" + secretKeyPrivate + '\'' +
                ", secretKeyPublic='" + secretKeyPublic + '\'' +
                ", createtime='" + createtime + '\'' +
                ", code='" + code + '\'' +
                ", scope='" + scope + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                '}';
    }
}
