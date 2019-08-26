package com.chinasofti.oauth2.client.util;

import javax.servlet.http.HttpSession;

import com.chinasofti.oauth2.client.model.CSIClientCertification;
import com.chinasofti.oauth2.client.model.MJsonObject;
import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 登录工具类
 * @author yangkai
 * @version 1.0
 * @since 1.0
 */
public class CSiUtil {
	public static ThreadLocal<HttpSession> sessions = new ThreadLocal<HttpSession>();//存放session
	
	public static OAuth2Config config;//CSIFilter.init的时候初始化
	
	/**
	 * 设置session到ThreadLocal中
	 * @param session
	 */
	public static void setSession(HttpSession session){
		sessions.set(session);
	}

	/**
	 * 清空ThreadLocal中的session
	 */
	public static void clearSession(){
		sessions.set(null);
	}


	/**
	 * 获取当前登录用户OPENID
	 * @return
	 */
	public static String getCurrentOpenId(){
		HttpSession session = sessions.get();
		if(session == null) return null;
		return (String) session.getAttribute(Constant.CSIOPENID);
	}

	/**
	 * 获取当前登录用户ID
	 * @return
	 */
	public static String getCurrentUserId(){
		HttpSession session = sessions.get();
		if(session == null) return null;
		return (String) session.getAttribute(Constant.CSIUSERID);
	}
	
	/**
	 * 获取当前登录账号
	 * @return
	 */
	public static String getCurrentUserName(){
		MJsonObject user = getCurrentUser();
		if(user == null) return "";
		return user.getJsonValue("username");
	}

	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static MJsonObject getCurrentUser(){
		String personStr = getCurrentUserStr();
		JsonObject personJson = new JsonParser().parse(personStr).getAsJsonObject();
		return new MJsonObject(personJson);
	}
	
	public static String getCurrentUserStr(){
		HttpSession session = sessions.get();
		if(session == null) return null;
		return String.valueOf(session.getAttribute(Constant.CSIUSER));
	}


	/**
	 * 获取当前令牌
	 * @return
	 */
	public static String getCurrentAsToken(){
		CSIClientCertification identity = getCurrentIdentity();
		if(identity == null) return "";
		return identity.getAccessToken();
	}

	/**
	 * 获取当前刷新令牌
	 * @return
	 */
	public static String getCurrentRsToken(){
		CSIClientCertification identity = getCurrentIdentity();
		if(identity == null) return "";
		return identity.getRefreshToken();
	}

	/**
	 * 获取当前令牌到期日期
	 * @return
	 */
	public static String getCurrentAsTokenExpir(){
		CSIClientCertification identity = getCurrentIdentity();
		if(identity == null) return null;
		return identity.getExpiresTime();
	}


	/**
	 * 获取当前CSIClientCertification
	 * @return
	 */
	public static CSIClientCertification getCurrentIdentity(){
		HttpSession session = sessions.get();
		if(session == null) return null;
		return (CSIClientCertification) session.getAttribute(Constant.SESSIONAUTHENTICATIONKEY);
	}

	public static void RemoveSession(){
		HttpSession session = sessions.get();
		session.removeAttribute(Constant.CSIUSER);
		session.removeAttribute(Constant.CSIUSERID);
		session.removeAttribute(Constant.CSIOPENID);
		session.removeAttribute(Constant.SESSIONAUTHENTICATIONKEY);

	}
}
