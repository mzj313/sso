package com.chinasofti.controller;

import com.chinasofti.oauth2.client.util.CSiUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by yangkai on 15/5/26.
 */
public class LogOutController extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CSiUtil.RemoveSession();
        CSiUtil.clearSession();
//        String redirectURL = CSiUtil.config.getRedirectURL();// 也可以是自己定义的其他回调路径
//		response.sendRedirect(CSiUtil.config.getLogoutURL() + "?redirect_url=" + redirectURL );
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
