package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.*;

/**
 * Created by KK on 2015/5/18.
 */
public interface OAuthDao {
    public void saveToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat);
    public TUser getUserByAccessToken(String token);
    public TAuthzApprovals getTAuthzApprovalsByToken(String token);
    public TAuthzApprovals getTAuthzApprovalsByRsToken(String rsToken);
}
