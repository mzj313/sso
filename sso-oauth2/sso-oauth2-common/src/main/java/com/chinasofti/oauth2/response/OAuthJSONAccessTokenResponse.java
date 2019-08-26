package com.chinasofti.oauth2.response;


import com.chinasofti.oauth2.common.OAuth;
import com.chinasofti.oauth2.common.error.OAuthError;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.token.BasicOAuthToken;
import com.chinasofti.oauth2.common.token.OAuthToken;
import com.chinasofti.oauth2.common.utils.JSONUtils;
import org.codehaus.jettison.json.JSONException;

/**
 * Created by yangkai on 15/5/20.
 */
public class OAuthJSONAccessTokenResponse extends OAuthAccessTokenResponse {

    public OAuthJSONAccessTokenResponse() {
    }

    @Override
    public String getAccessToken() {
        return getParam(OAuth.OAUTH_ACCESS_TOKEN);
    }

    @Override
    public Long getExpiresIn() {
        String value = getParam(OAuth.OAUTH_EXPIRES_IN);
        return value == null? null: Long.valueOf(value);
    }

    @Override
    public String getExpiration() {
        String value = getParam(OAuth.OAUTH_EXPiRATION);
        return value == null? null: value;
    }

    public String getScope() {
        return getParam(OAuth.OAUTH_SCOPE);
    }

    public OAuthToken getOAuthToken() {
        return new BasicOAuthToken(getAccessToken(), getExpiresIn(), getRefreshToken(), getScope());
    }

    public String getRefreshToken() {
        return getParam(OAuth.OAUTH_REFRESH_TOKEN);
    }

    protected void setBody(String body) throws OAuthProblemException {

        try {
            this.body = body;
            parameters = JSONUtils.parseJSON(body);
        } catch (JSONException e) {
            throw OAuthProblemException.error(OAuthError.CodeResponse.UNSUPPORTED_RESPONSE_TYPE,
                    "Invalid response! Response body is not " + OAuth.ContentType.JSON + " encoded");
        }
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }


    protected void setResponseCode(int code) {
        this.responseCode = code;
    }

}
