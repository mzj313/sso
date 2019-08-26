package com.chinasofti.oauth2.asserver;

/**
 */
public class Constants {

    public static String RESOURCE_SERVER_NAME = "server";
    public static final String INVALID_CLIENT_DESCRIPTION = "客户端验证失败，如错误的client_id/client_secret。";
    public static final String CLIENT_TOO_MANY_CONNECTION = "客户端多次验证失败,请稍后再试;";
    public static final String AUTHORIZATION_CODE_TOO_MANY_CONNECTION = "授权码多次验证失败,请稍后再试;";
    public static final String APPLICATION_ACCESS_DENIED = "您没有该应用访问权限";
    public static final String INVALID_AUTHORIZATION_CODE = "错误的授权码";
    public static final String AUTHORIZATION_CODE_IS_USED = "授权码已使用";
    public static final String REFRESH_TOKEN_TOO_MANY_CONNECTION = "刷新令牌多次验证失败,请稍后再试;";
    public static final String INVALID_REFRESH_TOKEN = "错误的刷新令牌";
    public static final String UNSUPPORTED_GRANT_TYPE = "不支持此种获取令牌方式";
    public static final String LOGOUT_ERROR = "Logout Failed";
    public static final String LOGOUT_TIMEOUT = "Session timeout";
    public static final String GET_TOKEN_ERROR = "获取token失败";
}
