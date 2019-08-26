package com.chinasofti.oauth2.asserver.filter;

import com.chinasofti.oauth2.asserver.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义实现logout，重写回调地址
 * Created by yangkai on 15/6/9.
 */
public class MyLogoutFilter extends LogoutFilter {
    private static final Logger logger = LoggerFactory
            .getLogger(MyLogoutFilter.class);
    protected String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String selfRedirectUrl = httpRequest.getParameter("redirectUrl");

        if(StringUtils.isNotBlank(selfRedirectUrl)){
            logger.info("登出回调地址为======>"+selfRedirectUrl);
            return selfRedirectUrl;
        }else{
            logger.info("登出回调地址为======>系统配置");
            return getRedirectUrl();
        }
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        String redirectUrl = getRedirectUrl(request, response, subject);
        //try/catch added for SHIRO-298:
        try {
            subject.logout();
            logger.info("登出成功======>");
        } catch (SessionException se){
            redirectUrl = appendError(redirectUrl, Constants.LOGOUT_TIMEOUT);
            logger.error("登出失败======>", se);
        } catch (Exception e) {
            redirectUrl = appendError(redirectUrl,Constants.LOGOUT_ERROR);
            logger.error("登出失败======>",e);
        }
        issueRedirect(request, response, redirectUrl);
        return false;
    }

    private String appendError(String url,String error){
        StringBuffer urlTerror = new StringBuffer(url);
        if(url.indexOf("?")>=0){
            urlTerror.append("&error=").append(error);
        }else{
            urlTerror.append("?error=").append(error);
        }
        return urlTerror.toString();
    }
}
