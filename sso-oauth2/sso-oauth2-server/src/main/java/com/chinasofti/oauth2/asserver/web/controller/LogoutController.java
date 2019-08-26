package com.chinasofti.oauth2.asserver.web.controller;

/**
 * Created by yangkai on 15/6/15.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.asserver.response.OAuthASResponse;
import com.chinasofti.oauth2.asserver.service.PhoneService;

/**
 */
@Controller
public class LogoutController {
	private static final org.slf4j.Logger logger = LoggerFactory
            .getLogger(LogoutController.class);

    @Autowired
    private PhoneService phoneService;

    @RequestMapping(value = "/app/logout",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String logout(HttpServletRequest req, Model model) throws OAuthSystemException{
        String phoneId = req.getParameter(Constant.HTTP_REQUEST_PARAM_PHONE_ID);
        logger.info("======手机端退出，phone_id=" + phoneId);
        if(StringUtils.isNotBlank(phoneId)){
            phoneService.removePhoneAuth(phoneId);
            //根据OAuthResponse生成ResponseEntity
            //生成OAuth响应
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setParam("message","退出成功")
                    .setParam("status","2000")
                    .buildJSONMessage();

            return response.getBody();
        }else{
            //根据OAuthResponse生成ResponseEntity
            //生成OAuth响应
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setParam("message", "退出失败,无phoneId")
                    .setParam("status", "3000")
                    .buildJSONMessage();
            return response.getBody();
        }

    }
}
