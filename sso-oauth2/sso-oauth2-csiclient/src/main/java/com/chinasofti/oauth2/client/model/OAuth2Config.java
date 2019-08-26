/**
 * 
 */
package com.chinasofti.oauth2.client.model;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yangkai
 *
 */
public class OAuth2Config {
	
	private String clientId;
	private String clientSecret;
	private String redirectURL;
	private String authorizeURL;
	private String accessTokenURL;
	private String apiURL;
	private String appURL;
	private String logoutURL;

	Properties p=new Properties();
	
	public OAuth2Config() {
		super();
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}


	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public void setAuthorizeURL(String authorizeURL) {
		this.authorizeURL = authorizeURL;
	}

	public void setAccessTokenURL(String accessTokenURL) {
		this.accessTokenURL = accessTokenURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public void setAppURL(String appURL) {
		this.appURL = appURL;
	}

	public String getAppURL() {
		return appURL;
	}

	public String getApiURL() {
		return apiURL;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public String getAuthorizeURL() {
		return authorizeURL;
	}

	public String getAccessTokenURL() {
		return accessTokenURL;
	}

	public String getProperty(String key){
		return p.getProperty(key);
	}

	public String getLogoutURL() {
		return logoutURL;
	}

	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}

	public OAuth2Config(String configFileName) throws IOException{
		p.load(getClass().getResourceAsStream("/"+configFileName));
		this.clientId=p.getProperty("client_id");
		this.clientSecret=p.getProperty("client_secret");
		this.redirectURL=p.getProperty("redirect_url");
		this.authorizeURL=p.getProperty("authorize_url");
		this.accessTokenURL=p.getProperty("access_token_url");
		this.apiURL=p.getProperty("api_url");
		this.appURL=p.getProperty("app_url");
		this.logoutURL=p.getProperty("logout_url");
	}
}
