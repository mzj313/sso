package com.ssyt.auth.handler;

import java.security.GeneralSecurityException;

import java.util.Collections;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import com.ssyt.auth.domain.UserLogin;
import com.ssyt.dao.UserDao;

public class CustomerHandler extends AbstractPreAndPostProcessingAuthenticationHandler {

	private  UserDao userDao;
	
    public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public CustomerHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
            Integer order) {
        super(name, servicesManager, principalFactory, order);
    }
    
    /**
     * 用于判断用户的Credential(换而言之，就是登录信息)，是否是俺能处理的
     * 就是有可能是，子站点的登录信息中不止有用户名密码等信息，还有部门信息的情况
     */
    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof UsernamePasswordCredential;
    }

    /**
     * 用于授权处理
     */
    @Override
    protected HandlerResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {
        UsernamePasswordCredential usernamePasswordCredentia = (UsernamePasswordCredential) credential;

        //获取传递过来的用户名和密码
        String username = usernamePasswordCredentia.getUsername();
        String password = usernamePasswordCredentia.getPassword();

        UserLogin user = userDao.getUserByUsername(username);

        if(user.getPassword().equals(password))
           //允许登录，并且通过this.principalFactory.createPrincipal来返回用户属性
           return createHandlerResult(credential, this.principalFactory.createPrincipal(username, Collections.emptyMap()), null);
        return null;
    }

}
