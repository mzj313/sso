package com.chinasofti.oauth2.client;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.client.util.SignUtils;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.exception.SignCreationException;
import com.chinasofti.oauth2.request.OAuthClientRequest;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthAccessTokenResponse;
import com.chinasofti.oauth2.response.OAuthClientResponse;
import com.chinasofti.oauth2.response.OAuthJSONAccessTokenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuzhuo on 2015/5/5.
 */
public class SignSecurityPrivateOAuthClient {
    protected HttpClient httpClient;
    protected String clientSecret;

    public SignSecurityPrivateOAuthClient(HttpClient oauthClient, String clientSecret) {
        this.clientSecret = clientSecret;
        this.httpClient = oauthClient;
    }

    public <T extends OAuthAccessTokenResponse> T accessToken(OAuthClientRequest request, Class<T> responseClass) throws OAuthSystemException, OAuthProblemException {
        return this.accessToken(request, "POST", responseClass);
    }

    public <T extends OAuthAccessTokenResponse> T accessToken(OAuthClientRequest request, String requestMethod, Class<T> responseClass) throws OAuthSystemException, OAuthProblemException {
        HashMap headers = new HashMap();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return (T) this.httpClient.execute(request, headers, requestMethod, responseClass);
    }

    public OAuthJSONAccessTokenResponse accessToken(OAuthClientRequest request) throws OAuthSystemException, OAuthProblemException {
        return (OAuthJSONAccessTokenResponse) this.accessToken(request, OAuthJSONAccessTokenResponse.class);
    }

    public OAuthJSONAccessTokenResponse accessToken(OAuthClientRequest request, String requestMethod) throws OAuthSystemException, OAuthProblemException {
        return (OAuthJSONAccessTokenResponse) this.accessToken(request, requestMethod, OAuthJSONAccessTokenResponse.class);
    }

    public <T extends OAuthClientResponse> T resource(OAuthClientRequest request, String requestMethod, Class<T> responseClass) throws OAuthSystemException, OAuthProblemException {
        return (T) this.httpClient.execute(request, (Map) null, requestMethod, responseClass);
    }

    public <T extends OAuthClientResponse> T resource(OAuthResourceClientRequest request, Class<T> responseClass)
            throws OAuthSystemException, OAuthProblemException, SignCreationException {
        try {
            request.setParameter(Constant.HTTP_REQUEST_PARAM_SIGN, SignUtils.createSign(request, clientSecret));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SignCreationException(e);
        }
        return (T) this.httpClient.execute(request.buildQueryMessage(), (Map) null, request.getMethod(), responseClass);
    }

    public void shutdown() {
        this.httpClient.shutdown();
    }
}
