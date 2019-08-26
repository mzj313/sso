package com.chinasofti.oauth2.common.parameters;


import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.common.message.OAuthMessage;

import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public interface OAuthParametersApplier {
    OAuthMessage applyOAuthParameters(OAuthMessage var1, Map<String, Object> var2) throws OAuthSystemException;
}
