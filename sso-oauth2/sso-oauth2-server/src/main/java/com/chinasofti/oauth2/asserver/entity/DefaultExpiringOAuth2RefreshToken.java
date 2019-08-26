package com.chinasofti.oauth2.asserver.entity;

import java.util.Date;

/**TODO 默认带有过期时间的refresh token类
 * Created by yangkai on 15/5/7.
 */
public class DefaultExpiringOAuth2RefreshToken extends DefaultOAuth2RefreshToken implements ExpiringOAuth2RefreshToken {
    private final Date expiration;

    /**
     * @param value
     */
    public DefaultExpiringOAuth2RefreshToken(String value, Date expiration) {
        super(value);
        this.expiration = expiration;
    }

    /**
     * The instant the token expires.
     *
     * @return The instant the token expires.
     */
    public Date getExpiration() {
        return expiration;
    }

    /**
     * Convenience method for checking expiration
     *
     * @return true if the expiration is befor ethe current time
     */
    public boolean isExpired() {
        return expiration != null && expiration.before(new Date());
    }

}
