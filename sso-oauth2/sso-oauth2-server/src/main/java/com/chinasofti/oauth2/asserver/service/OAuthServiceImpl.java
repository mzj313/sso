package com.chinasofti.oauth2.asserver.service;

import com.chinasofti.oauth2.asserver.dao.OAuthDao;
import com.chinasofti.oauth2.asserver.dao.TAuthzApprovalsDao;
import com.chinasofti.oauth2.asserver.dao.UserDao;
import com.chinasofti.oauth2.asserver.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Service
public class OAuthServiceImpl implements OAuthService {

    private Cache cache;

    @Autowired
    private ClientService clientService;

    @Autowired
    private OAuthDao authDao;

    @Autowired
    private TAuthzApprovalsDao tAuthzApprovalsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    public OAuthServiceImpl(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("code-cache");
    }

    @Override
    public void addAuthCode(String authCode, String username) {
        cache.put(authCode, username);
    }


    @Override
    public void addToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat){
        authDao.saveToken(gAccessToken, gReAccessToken, userId, clientId, expiresat);
    }


    @Override
    // TODO 重写生成accessToken方法
    //将token存入数据库
    public void addAccessToken(String gAccessToken, String gReAccessToken,String userId,String clientId,long expiresat) {
       /* Map<String,Object> value = new HashMap<String,Object>();
        value.put("accessToken",accessToken);
        value.put("user",user);
        value.put("client",client);
        cache.put(gAccessToken, value);*/

        authDao.saveToken(gAccessToken,gReAccessToken,userId,clientId,expiresat);

    }

    @Override
    // TODO 重写生成refreshToken方法
    public void addRefreshToken(String gRefreshToken, OAuth2RefreshToken refreshToken,User user,Client client) {
        Map<String,Object> value = new HashMap<String,Object>();
        value.put("refreshToken",refreshToken);
        value.put("user",user);
        value.put("client",client);
        cache.put(gRefreshToken, value);
    }

    @Override
    public String getUsernameByAuthCode(String authCode) {

//        return (String)cache.get(authCode).get();
        String userId = tAuthzApprovalsDao.checkAuthCode(authCode).getUserId();
        return userDao.findOne(userId).getUsername();
    }

    @Override
    public TUser getUserByAccessToken(String accessToken) {
        TUser tUser=authDao.getUserByAccessToken(accessToken);
        if(tUser!=null){
            return tUser;
        }else{
            return null;
        }
    }

    @Override
    public  boolean isGetToken(String authCode){
        TAuthzApprovals tAuthzApprovals=tAuthzApprovalsDao.checkAuthCode(authCode);
        if(tAuthzApprovals.getIsUsedAsCode()==1){
            return true;
        }
        return false;
    }
    @Override
    public String getUserIdByAuthCode(String authCode) {
        String userId = "";
        TAuthzApprovals tAuthzApprovals = tAuthzApprovalsDao.checkAuthCode(authCode);
        if(tAuthzApprovals!=null){
            userId = tAuthzApprovals.getUserId();
        }
        return userId;
    }

    @Override
    public String getUserIdByRefreshToken(String refreshToken){
        String userId = "";
        TAuthzApprovals tAuthzApprovals = tAuthzApprovalsDao.getByRefreshToken(refreshToken);
        if(tAuthzApprovals!=null){
            userId = tAuthzApprovals.getUserId();
        }
        return userId;
    }

    @Override
    // TODO 新增通过refreshToken获取用户名方法
    public String getUsernameByRefreshToken(String refreshToken) {
        Map<String,Object> value = null;
        User user = null;
        value = (Map<String, Object>) cache.get(refreshToken).get();
        if(value!=null){
            user = (User) value.get("user");
            return user.getUsername();
        }else{
            return null;
        }
    }

    @Override
    public boolean checkAuthCode(String authCode) {
        //return cache.get(authCode) != null;
        return tAuthzApprovalsDao.checkAuthCode(authCode) != null;
    }

    @Override
    // TODO 重写验证accessToken方法
    public boolean checkAccessToken(String accessToken) {
        boolean flag = false;
        //tAuthzApprovals 中含有token创建时间tAuthzApprovals.getExpiresat()
        TAuthzApprovals tAuthzApprovals=authDao.getTAuthzApprovalsByToken(accessToken);
        if(tAuthzApprovals!=null){
            //client 中含有token的有效时间client.getAccessTokenValidity()
            Client client=clientService.findByClientId(tAuthzApprovals.getClientId());
            long expiresat=tAuthzApprovals.getExpiresat();
            long validity=client.getAccessTokenValidity();
            long nowTime=System.currentTimeMillis()/1000;
            if(expiresat+validity>=nowTime){
                flag=true;
            }
        }
        return flag;
    }

    @Override
    // TODO 重写验证refreshToken方法
    public boolean checkRefreshToken(String refreshToken) {
        boolean flag = false;
        TAuthzApprovals tAuthzApprovals=authDao.getTAuthzApprovalsByRsToken(refreshToken);
        if(tAuthzApprovals!=null){
            //client 中含有token的有效时间client.getAccessTokenValidity()
            Client client=clientService.findByClientId(tAuthzApprovals.getClientId());
            long expiresat=tAuthzApprovals.getExpiresat();
            long validity=client.getRefreshTokenValidity();
            long nowTime=System.currentTimeMillis()/1000;
            if(expiresat+validity>=nowTime){
                flag=true;
            }
        }
        return flag;
    }

    @Override
    public boolean isHasAppAccess(String clientId, String userId) {
        return tAuthzApprovalsDao.findAppAccess(clientId,userId ) != null;
    }

    @Override
    public void addAsCode(String asCode, String clientId, String userId) {
        TAuthzApprovals tAuthzApprovals = new TAuthzApprovals();
        tAuthzApprovals.setAsCode(asCode);
        tAuthzApprovals.setClientId(clientId);
        tAuthzApprovals.setUserId(userId);
        tAuthzApprovals.setIsUsedAsCode(0);
        tAuthzApprovalsDao.updateAsCode(tAuthzApprovals);
    }

    @Override
    public boolean checkClientId(String clientId) {
        return clientService.findByClientId(clientId) != null;
    }

    @Override
    public boolean checkClientSecret(String clientSecret) {
        return clientService.findByClientSecret(clientSecret) != null;
    }

    @Override
    public long getExpireIn() {
        return 3600L;
    }

    @Override
    // TODO 新增获取默认refresh token过期时间
    public long getRTExpireIn() {
        return 2*3600L;
    }
}
