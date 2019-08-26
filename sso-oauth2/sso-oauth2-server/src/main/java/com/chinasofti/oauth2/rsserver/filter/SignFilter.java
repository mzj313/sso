package com.chinasofti.oauth2.rsserver.filter;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.ResourceRequest;
import com.chinasofti.oauth2.key.ApplicationKeyManager;
import com.chinasofti.oauth2.rsserver.SignatureUtil;
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
public class SignFilter extends AccessControlFilter {
    Logger logger = Logger.getLogger(this.getClass());


    private ApplicationKeyManager appKeyManager;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        // TODO 验证签名 成功返回true 失败返回false
        logger.debug("in isAccessAllowed");
        logger.info("进入签名验证拦截器===>开始验证");
        logger.info("in isAccessAllowed===>begin validate");
        String paramSign = servletRequest.getParameter(Constant.HTTP_REQUEST_PARAM_SIGN);
        // 验证参数正确性
        if (paramSign == null || paramSign.trim().length() == 0) {
            throw OAuthProblemException.error("invalid_request").description("param [sign] does not exists.");
        }

        ResourceRequest request = SignatureUtil.getResourceRequest((HttpServletRequest) servletRequest);
       // return SignUtils.verifySign(paramSign, request);
        if(!SignatureUtil.checkSignature(request.toString1(), paramSign)){
            logger.info("签名验证拦截器===>验证失败");
            logger.info("sing filter===>validate is failed");
            return false;
        }
        logger.info("签名验证拦截器===>验证成功");
        logger.info("sing filter===>validate is success");
        return true;
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
