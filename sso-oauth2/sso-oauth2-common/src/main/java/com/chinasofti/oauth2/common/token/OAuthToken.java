package com.chinasofti.oauth2.common.token;

/**
 * Created by yangkai on 15/5/20.
 */
public interface OAuthToken {
    String getAccessToken();

    Long getExpiresIn();

    String getRefreshToken();

    String getScope();
}
