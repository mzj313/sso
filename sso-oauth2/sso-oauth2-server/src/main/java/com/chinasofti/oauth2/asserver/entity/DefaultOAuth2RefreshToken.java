package com.chinasofti.oauth2.asserver.entity;



import java.io.Serializable;

/**TODO 默认不带有过期时间的refresh token类
 * Created by yangkai on 15/5/7.
 */
public class DefaultOAuth2RefreshToken implements Serializable,OAuth2RefreshToken {
    private String value;

    /**
     * Create a new refresh token.
     */
    public DefaultOAuth2RefreshToken(String value) {
        this.value = value;
    }

    /**
     * Default constructor for JPA and other serialization tools.
     */
    @SuppressWarnings("unused")
    private DefaultOAuth2RefreshToken() {
        this(null);
    }

    /* (non-Javadoc)
     * @see org.springframework.security.oauth2.common.IFOO#getValue()
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultOAuth2RefreshToken)) {
            return false;
        }

        DefaultOAuth2RefreshToken that = (DefaultOAuth2RefreshToken) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
