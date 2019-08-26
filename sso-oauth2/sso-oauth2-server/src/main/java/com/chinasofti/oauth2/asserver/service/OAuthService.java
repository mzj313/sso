package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.entity.*;

/**
 */
public interface OAuthService {

    //添加 auth code
    public void addAuthCode(String authCode, String username);

    //添加token  accesstoken  和  refreshtoken
    public void addToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat);
    //添加 access token
    // TODO 重写生成accessToken方法
    public void addAccessToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat);

    //添加 refresh token
    // TODO 重写生成refreshToken方法
    public void addRefreshToken(String gRefreshToken, OAuth2RefreshToken refreshToken,User user,Client client);

    //验证auth code是否有效
    boolean checkAuthCode(String authCode);
    //验证access token是否有效
    boolean checkAccessToken(String accessToken);
    //验证refresh token是否有效
    boolean checkRefreshToken(String refreshToken);

    String getUsernameByAuthCode(String authCode);
    TUser getUserByAccessToken(String accessToken);

    String getUserIdByAuthCode(String authCode);

    boolean isGetToken(String authCode);

    String getUserIdByRefreshToken(String refreshToken);
    // TODO 新增通过refreshToken获取用户名方法
    String getUsernameByRefreshToken(String refreshToken);


    //auth code / access token 过期时间
    long getExpireIn();

    //refresh token 过期时间
    // TODO 新增获取默认refresh token过期时间
    long getRTExpireIn();


    public boolean checkClientId(String clientId);

    public boolean checkClientSecret(String clientSecret);

    //判读用户和应用之间的权限关系
    public boolean isHasAppAccess(String clientId,String userId);

    //第一次生成授权码
    public void addAsCode(String asCode,String clientId,String userId);


}
