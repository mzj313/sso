package com.chinasofti.oauth2.asserver.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Created by KK on 2015/5/28.
 * 扩展默认的用户认证的bean
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {
    private static final long serialVersionUID = 1L;

    private String captcha;
    private boolean isAvailable=true;

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public UsernamePasswordCaptchaToken() {
        super();

    }

    public UsernamePasswordCaptchaToken(String username, char[] password,
                                        boolean rememberMe, String host, String captcha) {
        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    public UsernamePasswordCaptchaToken(String username, char[] password,
                                        boolean rememberMe, String host, boolean isAvailable) {
        super(username, password, rememberMe, host);
        this.isAvailable = isAvailable;
    }
}
