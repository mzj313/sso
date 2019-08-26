package com.chinasofti.oauth2.asserver.securitycheck;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证者
 * Created by xueyong on 15/6/12.
 */
public class Identifier {

    private SecurityCheck securityCheck;

    public Identifier(SecurityCheck securityCheck){
        this.securityCheck = securityCheck;
    }

    /**
     * 验证失败的时候 累加计数器
     * @param request
     * @return
     */
    public boolean isReplyToken(HttpServletRequest request){
       return  securityCheck.isReplyToken(request);
    }

    /**
     * 用户功能访问时清空计数器
     * @param request
     */
    public void removeKey(HttpServletRequest request){
        securityCheck.removeKey(request);
    }

    /**
     * 验证是否在拦截有效时间内
     * @param request
     * @return
     */
    public boolean verificationTime(HttpServletRequest request){
        return securityCheck.isFailureByIntercept(request);
    }
}
