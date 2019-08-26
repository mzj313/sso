package com.chinasofti.oauth2.request;


import com.chinasofti.oauth2.common.OAuth;

/**
 * Created by yangkai on 15/5/20.
 */
public class OAuthBearerClientRequest extends OAuthClientRequest.OAuthRequestBuilder {

    public OAuthBearerClientRequest(String url) {
        super(url);
    }

    public OAuthBearerClientRequest setAccessToken(String accessToken) {
        this.parameters.put(OAuth.OAUTH_BEARER_TOKEN, accessToken);
        return this;
    }

}
