package com.chinasofti.oauth2.asserver.entity;

import java.io.Serializable;



public class StrategyAppAccess implements Serializable{


	
	private String id;  //主键
	private String appId;  //应用ID
	private String grantid; //对应类型的ID
	private String type;  //类型 1用户 2机构 3组织
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getGrantid() {
		return grantid;
	}

	public void setGrantid(String grantid) {
		this.grantid = grantid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
