package com.chinasofti.oauth2.rsserver.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinasofti.oauth2.asserver.entity.UserEmailTel;
import com.chinasofti.oauth2.asserver.service.UserEmailTelService;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chinasofti.oauth2.asserver.entity.TUser;
import com.chinasofti.oauth2.asserver.service.OAuthService;

import java.io.IOException;
import java.net.URLDecoder;

/**
 */
@RestController
public class UserInfoController {
	private static final org.slf4j.Logger logger = LoggerFactory
            .getLogger(UserInfoController.class);

    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private UserEmailTelService userEmailTelService;

    @RequestMapping("/userInfo")
    public HttpEntity userInfo(HttpServletRequest request) throws OAuthSystemException {
    	long startTime = System.currentTimeMillis();
        //返回用户名
        //构建OAuth资源请求
        OAuthAccessResourceRequest oauthRequest = null;
        try {
            oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        //获取Access Token
        String accessToken = oauthRequest.getAccessToken();
        //TODO 检测accessToken是否有效(交由拦截器处理)
        //oAuthService.checkAccessToken(accessToken);
        TUser user = oAuthService.getUserByAccessToken(accessToken);
        long endTime = System.currentTimeMillis();
        logger.info("======根据令牌获取用户信息接口总耗时：" + (endTime-startTime) + "ms");
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/emailTel" , method = RequestMethod.GET)
    public HttpEntity emailtel(HttpServletRequest request) throws OAuthSystemException {
        long startTime = System.currentTimeMillis();

        //获取userId
        String userId = request.getParameter("userId");
        UserEmailTel emailTel = userEmailTelService.fingOne(userId);
        long endTime = System.currentTimeMillis();
        logger.info("======根据令牌获取用户信息接口总耗时：" + (endTime - startTime) + "ms");
        return new ResponseEntity(emailTel, HttpStatus.OK);
    }

    @RequestMapping(value = "/prefect")
    public void prefect(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String userId = request.getParameter("userId");
        //根据id获取用户的信息
        UserEmailTel uet = userEmailTelService.findAccout(userId);
        String targetURL = URLDecoder.decode(request.getParameter("targetURL"), "utf-8");
        //将targetURL 放入session
        request.getSession().setAttribute("targetURL", targetURL);
        response.sendRedirect("prefect.jsp?userId=" + userId + "&&username=" + uet.getUsername() + "&&account=" + uet.getAccount());
    }


    /**
     * 完善 用户email  tel 信息
     * @param userId
     * @param email
     * @param tel
     * @param code
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "connection")
    public void connection(String userId,String email,String tel,String code,String emailCode,HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        String result = userEmailTelService.update(userId, email, tel,code,emailCode);
        if(result==null){ //一切正常
            //重定向到用户请求路径
            response.sendRedirect((String) request.getSession().getAttribute("targetURL"));
        }else{
            response.getWriter().print(result);
        }

    }

    /**
     * 发送手机验证码
     * @param response
     * @param tel
     * @param userId
     */
    @RequestMapping(value = "sendCode")
    public void sendCode(HttpServletResponse response,String tel,String userId) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(userEmailTelService.sendCode(userId,tel));
    }

    /**
     * 发送手机验证码
     * @param response
     * @param email
     * @param userId
     */
    @RequestMapping(value = "sendEmailCode")
    public void sendEmailCode(HttpServletResponse response,String email,String userId) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print(userEmailTelService.sendEmailCode(userId, email));
    }
}
