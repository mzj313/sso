package com.chinasofti.oauth2.asserver.dao;

import com.chinasofti.oauth2.asserver.entity.TAuthzApprovals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Zhangjiaxing on 2015/5/15 0015.
 */
@Repository
public class TAuthzApprovalsDaoImpl implements TAuthzApprovalsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public TAuthzApprovals findAppAccess(String clientId, String userId) {
        String sql = "SELECT open_id,client_id,user_id,as_code,as_token,scope,expiresat,status,lastmodifiedat,is_used_as_code,refresh_token" +
                " FROM t_authz_approvals WHERE client_id=? AND user_id=?";
        List<TAuthzApprovals> accesses = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class),
                clientId,userId);
        if(accesses.size() == 0) {
            return null;
        }
        return accesses.get(0);
    }

    @Override
    public TAuthzApprovals updateAsCode(TAuthzApprovals tAuthzApprovals) {
        String sql = "update t_authz_approvals set as_code=? ,is_used_as_code=? where client_id=? and user_id=?";
        jdbcTemplate.update(
                sql,
                tAuthzApprovals.getAsCode(),tAuthzApprovals.getIsUsedAsCode(),
                tAuthzApprovals.getClientId(),tAuthzApprovals.getUserId());
        return tAuthzApprovals;
    }

    @Override
    public TAuthzApprovals checkAuthCode(String authCode) {
        String sql = "SELECT open_id,client_id,user_id,as_code,as_token,scope,expiresat,status,lastmodifiedat,is_used_as_code,refresh_token" +
                " FROM t_authz_approvals WHERE as_code=?";
        List<TAuthzApprovals> accesses = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class),
                authCode);
        if(accesses.size() == 0) {
            return null;
        }
        return accesses.get(0);
    }

    @Override
    public TAuthzApprovals getByRefreshToken(String refreshToken){
        String sql = "SELECT open_id,client_id,user_id,as_code,as_token,scope,expiresat,status,lastmodifiedat,is_used_as_code,refresh_token" +
                " FROM t_authz_approvals WHERE refresh_token=?";
        List<TAuthzApprovals> accesses = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class),
                refreshToken);
        if(accesses.size() == 0) {
            return null;
        }
        return accesses.get(0);

    }
}
