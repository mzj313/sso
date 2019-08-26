package com.chinasofti.controller;

import com.chinasofti.oauth2.client.CSIClient;
import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.chinasofti.oauth2.client.util.CSiUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangkai on 15/5/18.
 */
public class HomeController extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        String openId = CSiUtil.getCurrentOpenId();
        String username = CSiUtil.getCurrentUserName();
        String userId = CSiUtil.getCurrentUserId();
        String asToken = CSiUtil.getCurrentAsToken();
        String expir = CSiUtil.getCurrentAsTokenExpir();

        req.setAttribute("username",username);
        req.setAttribute("userId",userId);
        req.setAttribute("openId",openId);
        req.setAttribute("asToken",asToken);
        req.setAttribute("expir",expir);
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}
