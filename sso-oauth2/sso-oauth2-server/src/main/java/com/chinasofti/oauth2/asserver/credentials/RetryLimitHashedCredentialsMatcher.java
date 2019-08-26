package com.chinasofti.oauth2.asserver.credentials;

import com.chinasofti.oauth2.utils.MD5;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if(retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        //自定义密码验证方式
        boolean matches = verifyPassword(token,info);

        //原生态比较方式
//      boolean matches = super.doCredentialsMatch(token, info);

        if(matches) {
            //clear retry count
            passwordRetryCache.remove(username);
        }
        return matches;
    }


    private boolean verifyPassword(AuthenticationToken token, AuthenticationInfo info){
        boolean matches = false;
        char[] passwordChar = (char[])token.getCredentials();
        String password = new String(passwordChar);

        byte[] saltByte = ((SimpleAuthenticationInfo)info).getCredentialsSalt().getBytes();

        String salt = new String(saltByte);

        String passwordMD5 = MD5.getEncryptResult(password+"{"+salt+"}");

        String dsPwd =(String)info.getCredentials();

        if(passwordMD5.equals(dsPwd)){
            matches = true;
        }
        return matches;
    }
}
