package com.chinasofti.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Created by yangkai on 15/5/18.
 */
public class Oauth2IndexController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	String targetUrl = req.getParameter("target_url");
		if(targetUrl != null) {
			resp.sendRedirect(targetUrl);
			return;
		}
        resp.sendRedirect(req.getContextPath()+"/home");
    }
}
