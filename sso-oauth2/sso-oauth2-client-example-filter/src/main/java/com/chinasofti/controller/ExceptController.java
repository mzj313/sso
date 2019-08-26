package com.chinasofti.controller;

import com.chinasofti.oauth2.client.util.CSiUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yangkai on 15/7/30.
 */
public class ExceptController extends HttpServlet{
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

        String userId = CSiUtil.getCurrentUserId();

        req.setAttribute("usrId",userId);

        req.getRequestDispatcher("except.html").forward(req, resp);
    }
}
