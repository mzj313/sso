package com.chinasofti.oauth2.request;


import com.chinasofti.oauth2.common.OAuth;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.common.message.OAuthMessage;
import com.chinasofti.oauth2.common.parameters.OAuthParametersApplier;
import com.chinasofti.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public class ClientHeaderParametersApplier implements OAuthParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params)
            throws OAuthSystemException {

        String header = OAuthUtils.encodeAuthorizationBearerHeader(params);
        message.addHeader(OAuth.HeaderType.AUTHORIZATION, header);
        return message;

    }

}
