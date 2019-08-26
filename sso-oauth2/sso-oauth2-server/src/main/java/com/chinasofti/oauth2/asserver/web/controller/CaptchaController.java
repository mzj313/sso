package com.chinasofti.oauth2.asserver.web.controller;

import com.chinasofti.oauth2.asserver.utils.CaptchaUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by KK on 2015/5/28.
 */
@Controller
public class CaptchaController {
    private static final long serialVersionUID = -124247581620199710L;

    public static final String KEY_CAPTCHA = "SE_KEY_MM_CODE";

    @RequestMapping(value="/captcha",method = RequestMethod.GET)
    @ResponseBody
    public void getCode(HttpServletRequest request,HttpServletResponse response) {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);

        HttpSession session = request.getSession();
        String code=new CaptchaUtil().getVerificationCode(request, response);
        session.removeAttribute(KEY_CAPTCHA);
        session.setAttribute(KEY_CAPTCHA, code);
    }
}
