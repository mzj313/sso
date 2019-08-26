package com.chinasofti.oauth2.asserver.realm;

import com.chinasofti.oauth2.asserver.exception.CaptchaException;
import com.chinasofti.oauth2.asserver.service.UserService;
import com.chinasofti.oauth2.asserver.entity.User;
import com.chinasofti.oauth2.asserver.shiro.UsernamePasswordCaptchaToken;
import com.chinasofti.oauth2.asserver.web.controller.CaptchaController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    private boolean captchaEnabled = true;

    public void setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //暂时不加权限
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordCaptchaToken authcToken = (UsernamePasswordCaptchaToken) token;
        //两处都打开时才校验
        if(authcToken.isAvailable()) {
        	//增加判断验证码逻辑
        	if(captchaEnabled){
        		String captcha = authcToken.getCaptcha();
        		String exitCode = (String) SecurityUtils.getSubject().getSession()
        				.getAttribute(CaptchaController.KEY_CAPTCHA);
        		if (null == captcha || !captcha.equalsIgnoreCase(exitCode)) {
        			throw new CaptchaException();
        		}
        	}
        }

        //String username = (String)token.getPrincipal();
        String username=authcToken.getUsername();
        if(username==null){
        	throw new AccountException(
        			"Null usernames are not allowed by this realm.");
        }

        User user = userService.findByUsername(username);

        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getSalt()),//改造此参数，原有盐值是通过user.getCredentialsSalt()获取
                getName()  //realm name
        );
        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }


    /**
     * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
     */
    public static class ShiroUser implements Serializable {

        private static final long serialVersionUID = -1748602382963711884L;
        private String loginName;
        private String name;

        public ShiroUser(String loginName, String name) {
            this.loginName = loginName;
            this.name = name;
        }

        public String getLoginName() {
            return loginName;
        }

        /**
         * 本函数输出将作为默认的&lt;shiro:principal/&gt;输出.
         */
        @Override
        public String toString() {
            return loginName;
        }

        public String getName() {
            return name;
        }
    }

}
