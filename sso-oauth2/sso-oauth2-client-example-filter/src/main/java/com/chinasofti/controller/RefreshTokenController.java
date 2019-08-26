package com.chinasofti.controller;

import com.chinasofti.oauth2.client.SignSecurityOAuthClient;
import com.chinasofti.oauth2.client.URLConnectionClient;
import com.chinasofti.oauth2.client.model.CSIClientCertification;
import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.chinasofti.oauth2.client.util.CSiUtil;
import com.chinasofti.oauth2.client.util.Constant;
import com.chinasofti.oauth2.common.OAuth;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.common.message.types.GrantType;
import com.chinasofti.oauth2.request.OAuthClientRequest;
import com.chinasofti.oauth2.response.OAuthAccessTokenResponse;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yangkai on 15/7/2.
 */
public class RefreshTokenController extends HttpServlet {
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
        String rsToken = CSiUtil.getCurrentRsToken();
        OAuth2Config config = new OAuth2Config("OAuth2-config.properties");
        SignSecurityOAuthClient oAuthClient = new SignSecurityOAuthClient(new URLConnectionClient());

        OAuthClientRequest accessTokenRequest = null;
        try {
            accessTokenRequest = OAuthClientRequest
                    .tokenLocation(config.getAccessTokenURL())
                    .setGrantType(GrantType.REFRESH_TOKEN)
                    .setClientId(config.getClientId())
                    .setClientSecret(config.getClientSecret())
                    .setRefreshToken(rsToken)
                    .setRedirectURI(config.getRedirectURL())
                    .buildQueryMessage();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }

        OAuthAccessTokenResponse oAuthResponse = null;
        try {
            oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        String accessToken = oAuthResponse.getAccessToken();
        String refreshToken = oAuthResponse.getRefreshToken();
        String expiration = oAuthResponse.getExpiration();

        req.getSession().setAttribute(Constant.SESSIONAUTHENTICATIONKEY, new CSIClientCertification(userId, accessToken, refreshToken, expiration));

        resp.sendRedirect("home");
    }
}
