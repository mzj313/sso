package com.chinasofti.oauth2.validator;


import com.chinasofti.oauth2.common.OAuth;

/**
 * Created by yangkai on 15/5/20.
 */
public class TokenValidator extends OAuthClientValidator {

    public TokenValidator() {

        requiredParams.put(OAuth.OAUTH_ACCESS_TOKEN, new String[] {});

        notAllowedParams.add(OAuth.OAUTH_CODE);
    }
}
