package org.mzj.test.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import io.buji.pac4j.subject.Pac4jPrincipal;

public class ShiroUtils {
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static String getCurrentUserName() {
		String username = null;
		Subject subject = SecurityUtils.getSubject();

		if (subject.getPrincipals() == null) {
			return "";
		}

		Object pricipal = subject.getPrincipals().getPrimaryPrincipal();
		if (pricipal != null && pricipal instanceof Pac4jPrincipal) {
			Pac4jPrincipal pac4jPrincipal = (Pac4jPrincipal) subject.getPrincipals().getPrimaryPrincipal();
			username = pac4jPrincipal.getProfile().getId();
		}
		return username;
	}
}
