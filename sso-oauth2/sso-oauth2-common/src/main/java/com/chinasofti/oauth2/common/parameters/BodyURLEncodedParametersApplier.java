package com.chinasofti.oauth2.common.parameters;


import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.common.message.OAuthMessage;
import com.chinasofti.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public class BodyURLEncodedParametersApplier implements OAuthParametersApplier {
    public BodyURLEncodedParametersApplier() {
    }

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) throws OAuthSystemException {
        String body = OAuthUtils.format(params.entrySet(), "UTF-8");
        message.setBody(body);
        return message;
    }
}
