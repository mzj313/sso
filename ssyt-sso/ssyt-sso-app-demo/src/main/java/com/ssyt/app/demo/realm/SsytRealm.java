package com.ssyt.app.demo.realm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pac4j.core.profile.CommonProfile;

import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;

/**
 * pac4jReal默认的从sso服务登录时同时获取用户权限信息。 用户权限信息应该由各个接入sso的子系统自行完成，因此需要重新实现
 * 
 * @author huangshuqing
 *
 */
public class SsytRealm extends AuthorizingRealm {

	private String principalNameAttribute;

	public SsytRealm() {
		setAuthenticationTokenClass(Pac4jToken.class);
	}

	public String getPrincipalNameAttribute() {
		return principalNameAttribute;
	}

	public void setPrincipalNameAttribute(String principalNameAttribute) {
		this.principalNameAttribute = principalNameAttribute;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken)
			throws AuthenticationException {

		final Pac4jToken token = (Pac4jToken) authenticationToken;
		final LinkedHashMap<String, CommonProfile> profiles = token.getProfiles();

		final Pac4jPrincipal principal = new Pac4jPrincipal(profiles, principalNameAttribute);
		final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
		return new SimpleAuthenticationInfo(principalCollection, profiles.hashCode());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
		final Set<String> roles = new HashSet<>();
		final Set<String> permissions = new HashSet<>();
		final Pac4jPrincipal principal = principals.oneByType(Pac4jPrincipal.class);
		if (principal != null) {
			final List<CommonProfile> profiles = principal.getProfiles();
			for (CommonProfile profile : profiles) {
				if (profile != null) {
					//获取当前登录用户的username
					String username = profile.getId();
					System.out.println("current_user is :" + username);
					//以下应该从当前子系统获取，这里做写死处理
					List<String> ssytPermissionList = new ArrayList<>();
					ssytPermissionList.add("PERSON_LOAN");
					ssytPermissionList.add("COMPANY_LOAN");
					
					List<String> ssytRoleList = new ArrayList<>();
					ssytRoleList.add("DEPART_MANAGER");
					//
					roles.addAll(ssytRoleList);
					permissions.addAll(ssytPermissionList);
				}
			}
		}

		final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addRoles(roles);
		simpleAuthorizationInfo.addStringPermissions(permissions);
		return simpleAuthorizationInfo;
	}

}
