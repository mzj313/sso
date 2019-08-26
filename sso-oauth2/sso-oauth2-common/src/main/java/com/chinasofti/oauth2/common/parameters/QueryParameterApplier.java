package com.chinasofti.oauth2.common.parameters;


import com.chinasofti.oauth2.common.message.OAuthMessage;
import com.chinasofti.oauth2.common.utils.OAuthUtils;

import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public class QueryParameterApplier implements OAuthParametersApplier {
    public QueryParameterApplier() {
    }

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {
        String messageUrl = message.getLocationUri();
        if(messageUrl != null) {
            boolean containsQuestionMark = messageUrl.contains("?");
            StringBuffer url = new StringBuffer(messageUrl);
            StringBuffer query = new StringBuffer(OAuthUtils.format(params.entrySet(), "UTF-8"));
            if(!OAuthUtils.isEmpty(query.toString())) {
                if(containsQuestionMark) {
                    url.append("&").append(query);
                } else {
                    url.append("?").append(query);
                }
            }

            message.setLocationUri(url.toString());
        }

        return message;
    }
}
