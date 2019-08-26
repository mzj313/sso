package com.chinasofti.oauth2.asserver.filter;


import com.chinasofti.oauth2.asserver.shiro.UsernamePasswordCaptchaToken;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/** TODO 新增基于第三方应用跳转登陆实现
 * Created by yangkai on 15/4/29.
 */
public class Oauth2LoginFormAuthenticationFilter extends FormAuthenticationFilter{

    private  String asRedirectUrl = "";

    /**
     * 重写登录成功跳转方法
     * @param request
     * @param response
     * @throws Exception
     */
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest req = (HttpServletRequest)request;
        String asRedirectUrlBase64 = req.getParameter("asRedirectUrl");
        asRedirectUrl = Base64.decodeToString(asRedirectUrlBase64);
        if(OAuthUtils.isEmpty(asRedirectUrl)){
            this.setSuccessUrl("/whereto");
        }else{
            this.setSuccessUrl(asRedirectUrl);
        }

        WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
    }


    /**
     * 重写登录失败跳转方法
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        setFailureAttribute(request, e);
        request.setAttribute("asRedirectUrl", request.getParameter("asRedirectUrl"));
        request.setAttribute("clientName",request.getParameter("clientName"));
        //login failed, let request continue back to the login page:
        return true;
    }



    //新增验证码功能代码－start

    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    public String getCaptchaParam() {

        return captchaParam;

    }

    protected String getCaptcha(ServletRequest request) {

        return WebUtils.getCleanParam(request, getCaptchaParam());

    }

    protected AuthenticationToken createToken(

            ServletRequest request, ServletResponse response) {

        String username = getUsername(request);

        String password = getPassword(request);

        String captcha = getCaptcha(request);

        boolean rememberMe = isRememberMe(request);

        String host = getHost(request);

        return new UsernamePasswordCaptchaToken(username,
                password.toCharArray(), rememberMe, host, captcha);

    }
    //新增验证码功能代码－end

}
