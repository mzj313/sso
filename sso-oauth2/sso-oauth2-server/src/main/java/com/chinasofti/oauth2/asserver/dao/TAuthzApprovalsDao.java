package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.TAuthzApprovals;

/**
 * Created by Zhangjiaxing on 2015/5/15 0015.
 */
public interface TAuthzApprovalsDao {

    TAuthzApprovals findAppAccess(String clientId,String userId);

    TAuthzApprovals updateAsCode(TAuthzApprovals tAuthzApprovals);

    TAuthzApprovals checkAuthCode(String authCode);

    TAuthzApprovals getByRefreshToken(String refreshToken);

}
