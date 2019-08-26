package com.chinasofti.oauth2.client;


import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.request.OAuthClientRequest;
import com.chinasofti.oauth2.response.OAuthClientResponse;

import java.util.Map;

/**
 * Created by yangkai on 15/5/20.
 */
public interface HttpClient {

    public <T extends OAuthClientResponse> T execute(
            OAuthClientRequest request,
            Map<String, String> headers,
            String requestMethod,
            Class<T> responseClass)
            throws OAuthSystemException, OAuthProblemException;

    /**
     * Shut down the client and release the resources associated with the HttpClient
     */
    public void shutdown();
}
