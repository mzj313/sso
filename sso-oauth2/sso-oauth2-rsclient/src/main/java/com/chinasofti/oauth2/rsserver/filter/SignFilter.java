package com.chinasofti.oauth2.rsserver.filter;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.ResourceRequest;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.rsserver.securitycheck.SecurityCheck;
import com.chinasofti.oauth2.rsserver.util.SignatureUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by yangkai on 15/5/21.
 */
public class SignFilter implements Filter {


    private static Logger logger= LoggerFactory.getLogger(SignFilter.class);

    private SecurityCheck signSecurityCheck ;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        signSecurityCheck = (SecurityCheck)ctx.getBean("signSecurityCheck");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String paramSign = servletRequest.getParameter(Constant.HTTP_REQUEST_PARAM_SIGN);
            // 验证参数正确性
            if (paramSign == null || paramSign.trim().length() == 0) {
                throw OAuthProblemException.error("invalid_request").description("param [sign] does not exists.");
            }

            if(isFailureByIntercept((HttpServletRequest)servletRequest)){
                logger.info("没有超过拦截限制时间");
                callBackMsg(response, "IP访问限制",1007);
                return ;
            }

            ResourceRequest request = SignatureUtil.getResourceRequest((HttpServletRequest) servletRequest);
            boolean flag = SignatureUtil.checkSignature(request.toString1(), paramSign);
            if (flag) {
                logger.info("SignFilter 进入签名验证成功----url:" + ((HttpServletRequest) servletRequest).getRequestURI());
                setNormalIpCount((HttpServletRequest) servletRequest);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }else{
                isReplyToken((HttpServletRequest)servletRequest);
            }

            callBackMsg(response,"签名验证失败",1008);


        }catch (Exception e){
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }

    private boolean isFailureByIntercept(HttpServletRequest servletRequest){
        return signSecurityCheck.isFailureByIntercept(servletRequest);
    }

    private void setNormalIpCount(HttpServletRequest servletRequest){
        signSecurityCheck.removekey(servletRequest);
    }

    private boolean isReplyToken(HttpServletRequest servletRequest){
        return signSecurityCheck.isReplyToken(servletRequest);
    }
    private void callBackMsg(HttpServletResponse response,String message,int result){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        JSONObject back = new JSONObject();
        PrintWriter out = null;
        try {
            back.put("result",result);
            back.put("message",message);
            back.put("total",0);
            back.put("rows", new JSONArray());
            out = response.getWriter();
            out.append(back.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out != null) {
                out.close();
            }
        }
        return;
    }

    @Override
    public void destroy() {

    }
}
