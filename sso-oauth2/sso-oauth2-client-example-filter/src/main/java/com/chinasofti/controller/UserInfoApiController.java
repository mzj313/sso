package com.chinasofti.controller;

import com.chinasofti.oauth2.client.SignSecurityOAuthClient;
import com.chinasofti.oauth2.client.URLConnectionClient;
import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.chinasofti.oauth2.client.util.CSiUtil;
import com.chinasofti.oauth2.common.OAuth;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthResourceResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yangkai on 15/5/22.
 */
public class UserInfoApiController extends HttpServlet {
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
        JsonObject person;
        JsonParser parser = new JsonParser();
        String userId = CSiUtil.getCurrentUserId();
        String asToken = CSiUtil.getCurrentAsToken();
        OAuth2Config config = new OAuth2Config("OAuth2-config.properties");
        SignSecurityOAuthClient oAuthClient = new SignSecurityOAuthClient(new URLConnectionClient());

        OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("user_info", config.getApiURL()+"/api/users", OAuth.HttpMethod.GET);
        userInfoRequest.setAccessToken(asToken);
        userInfoRequest.setParameter("id",userId);
        OAuthResourceResponse resourceResponse = null;
        try {
            resourceResponse  = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String out = resourceResponse.getBody();
        person = parser.parse(out).getAsJsonObject();

        req.setAttribute("person",person.toString());
        req.getRequestDispatcher("currentUser.jsp").forward(req, resp);
    }
}
