package com.chinasofti.oauth2.rsserver.filter;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.ResourceRequest;
import com.chinasofti.oauth2.key.ApplicationKey;
import com.chinasofti.oauth2.key.ApplicationKeyManager;
import com.chinasofti.oauth2.rsserver.SignUtils;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 签名拦截器，用于拦截并验证签名
 * Created by liuzhuo on 2015/4/29.
 */
public class SignByPPFilter extends AccessControlFilter {
    Logger logger = Logger.getLogger(this.getClass());


    private ApplicationKeyManager appKeyManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        // TODO 验证签名 成功返回true 失败返回false
        logger.debug("in isAccessAllowed");
        String signSrc = servletRequest.getParameter(Constant.HTTP_REQUEST_PARAM_SIGN);
        String clientid = servletRequest.getParameter(Constant.HTTP_REQUEST_PARAM_CLIENT_ID);
        // 验证参数正确性
        if (signSrc == null || signSrc.trim().length() == 0) {
            throw OAuthProblemException.error("invalid_request").description("param [sign] does not exists.");
        }
        if (clientid == null || clientid.trim().length() == 0) {
            throw OAuthProblemException.error("invalid_request").description("param [client_id] does not exists.");
        }

        // 获得当前应用的公钥并对参数进行解密
        ApplicationKey key = appKeyManager.getApplicationKey(clientid);
        String paramSign = servletRequest.getParameter(Constant.HTTP_REQUEST_PARAM_SIGN);
        ResourceRequest request = SignUtils.getResourceRequest((HttpServletRequest) servletRequest);
        return SignUtils.verifySign(paramSign, request, key.getPrivateKey());
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // TODO 返回false，之前调用安全审计模块计入安全审计信息
        logger.debug("in onAccessDenied");
        return false;
    }

    protected void setAppKeyManager(ApplicationKeyManager appKeyManager) {
        this.appKeyManager = appKeyManager;
    }

    protected ApplicationKeyManager getAppKeyManager() {
        return this.appKeyManager;
    }
}
