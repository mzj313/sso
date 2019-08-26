package com.chinasofti.oauth2.client.util;

/**
 * Created by yangkai on 15/5/20.
 */
public class Constant {
    public static final String CSIUSERID = "_csi_user_id";
    public static final String CSIOPENID = "_csi_open_id";
    public static final String CSIUSER = "_csi_user";
    public static final String ACCESS = "_access_token";
    public static final String SESSIONAUTHENTICATIONKEY="csi_authenticate_key";
    public static final String TENANT = "tenant";
    public static final String ISV = "_isv_name";
    public static final String ORIGINALURL = "_Original_URL";
    public static final String charset = "utf-8";
    public static final String COOKIE_SEPERATOR = "|";
    public static final String COOKIE_NAME="RCLOUD";
    public static final String OAUTH_STATE="oauth_state";
    public static final String TEMPLATE_REPLACE_STRING ="${error}";
    public static final String TEMPLATE_NAME="error-client.html";
    public static final String STATE_ERROR="验证state失败";
    public static final String GET_ACCESSTOKEN_ERROR="客户端获取令牌失败";
    public static final String GET_USERINFO_ERROR="客户端获取用户基本信息失败";
}
