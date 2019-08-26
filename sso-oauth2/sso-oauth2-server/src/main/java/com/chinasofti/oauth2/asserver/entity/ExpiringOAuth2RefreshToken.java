package com.chinasofti.oauth2.asserver.entity;

import java.util.Date;

/**带有过期时间refresh token 接口类
 * Created by yangkai on 15/5/7.
 */
public interface ExpiringOAuth2RefreshToken extends OAuth2RefreshToken {
    Date getExpiration();
    boolean isExpired();
}
