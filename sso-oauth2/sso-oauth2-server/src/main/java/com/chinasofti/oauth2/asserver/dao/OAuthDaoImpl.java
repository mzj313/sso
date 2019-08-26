package com.chinasofti.oauth2.asserver.dao;


import com.chinasofti.oauth2.asserver.entity.TAuthzApprovals;
import com.chinasofti.oauth2.asserver.entity.TUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import java.util.List;

/**
 * Created by KK on 2015/5/18.
 */
@Repository
public class OAuthDaoImpl implements OAuthDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat) {
        String sql = "update t_authz_approvals set as_token=? ,scope=? ,expiresat=?,status=?,lastmodifiedat=?,is_used_as_code=?,refresh_token=? where client_id=? and user_id=?";
        long date=System.currentTimeMillis()/1000;
        jdbcTemplate.update(
                sql,
                gAccessToken,"",expiresat/1000,"",date,1,gReAccessToken,
                clientId,userId);
    }

    @Override
    public TUser getUserByAccessToken(String token) {
        String sql="SELECT open_id,user_id FROM t_authz_approvals WHERE as_token=?";
        List<TAuthzApprovals> accesses =jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class), token);
        if (accesses.size()==0){
            return null;
        }
        String sql2="select id user_id,username from t_user where id=?";
        List<TUser> tUsers=jdbcTemplate.query(sql2,new BeanPropertyRowMapper(TUser.class),accesses.get(0).getUserId());

        if(tUsers.size()==0){
            return  null;
        }
        tUsers.get(0).setOpenId(accesses.get(0).getOpenId());
        return tUsers.get(0);
    }

    @Override
    public TAuthzApprovals getTAuthzApprovalsByToken(String token) {
        String sql="SELECT open_id,client_id,user_id,as_code,as_token,scope,expiresat,status,lastmodifiedat,is_used_as_code,refresh_token FROM t_authz_approvals WHERE  as_token=?";
        List<TAuthzApprovals> accesses =jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class), token);
        if (accesses.size()==0){
            return null;
        }
        return accesses.get(0);
    }

    @Override
    public TAuthzApprovals getTAuthzApprovalsByRsToken(String rsToken){
        String sql="SELECT open_id,client_id,user_id,as_code,as_token,scope,expiresat,status,lastmodifiedat,is_used_as_code,refresh_token FROM t_authz_approvals WHERE  refresh_token=?";
        List<TAuthzApprovals> accesses =jdbcTemplate.query(sql, new BeanPropertyRowMapper(TAuthzApprovals.class), rsToken);
        if (accesses.size()==0){
            return null;
        }
        return accesses.get(0);
    }
}
