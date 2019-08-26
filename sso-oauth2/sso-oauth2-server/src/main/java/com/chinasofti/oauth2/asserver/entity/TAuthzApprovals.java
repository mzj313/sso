package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;

/**
 * @ClassName: TAuthzApprovals
 * @author zhangjiaxing
 * @date 2015年5月13日 下午5:02:41
 *
 */
public class TAuthzApprovals implements Serializable{

	
	private String openId;  //使用base64加密 userId+'@'+clientId
	private String clientId;
	private String userId;
	private String asCode;
	private String asToken;
	private String scope;
	private Integer expiresat;
	private String status;
	private Integer lastmodifiedat;
	private Integer isUsedAsCode;
	private String refreshToken;  //刷新令牌
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAsToken() {
		return asToken;
	}
	public void setAsToken(String asToken) {
		this.asToken = asToken;
	}
	public String getAsCode() {
		return asCode;
	}
	public void setAsCode(String asCode) {
		this.asCode = asCode;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public Integer getExpiresat() {
		return expiresat;
	}
	public void setExpiresat(Integer expiresat) {
		this.expiresat = expiresat;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getLastmodifiedat() {
		return lastmodifiedat;
	}
	public void setLastmodifiedat(Integer lastmodifiedat) {
		this.lastmodifiedat = lastmodifiedat;
	}
	public Integer getIsUsedAsCode() {
		return isUsedAsCode;
	}
	public void setIsUsedAsCode(Integer isUsedAsCode) {
		this.isUsedAsCode = isUsedAsCode;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
