package org.mzj.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chinasofti.oauth2.client.model.MJsonObject;
import com.google.gson.JsonParser;

public class APIAuthWebVersionTest extends TestBase {
	
	private String server_addr = authorize_url.substring(0,
			authorize_url.indexOf("/pup-asserver"))
			+ "/pup-asserver";
	
	private String username = "renbianji";
	private String password = "renbianji1";

	@Test
	public void test获取授权码(){
		try {
			getAsCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getAsCode() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("client_id", client_id);
		param.put("response_type", "code");
		param.put("redirect_uri", redirect_url);
		param.put("state", "xxx");
		param.put("username", username);
		param.put("password", password);
		param.put("available", "0");
		String res = httpReq(authorize_url, methodType, param);
		return res;
	}
	
	@Test
	public void test获取令牌(){
		MJsonObject jsonObj = getAsTokenJson();
		String refreshToken = jsonObj.getJsonValue("refresh_token");
		System.out.println("refresh_token=" + refreshToken);
		System.out.println("access_token=" + jsonObj.getJsonValue("access_token"));
	}
	private MJsonObject getAsTokenJson() {
		String ascode = "c94e49bdcfaedfc13a03fed790f3926c";
		Map<String, String> param = new HashMap<String, String>();
		param.put("client_id", client_id);
		param.put("client_secret", client_secret);
		param.put("grant_type", "authorization_code");
		param.put("redirect_uri", redirect_url);//json开头
		param.put("code", ascode);
		String res = httpReq(access_token_url, "GET", param);
		MJsonObject jsonObj = new MJsonObject(new JsonParser().parse(res).getAsJsonObject());
		return jsonObj;
	}
	
	@Test
	public void test刷新令牌(){
//		MJsonObject jsonObj = getAsTokenJson();
//		String refreshToken = jsonObj.getJsonValue("refresh_token");
		
		String refreshToken = "ErWrAKWMHuDRfyEQYNO3+hJN1XGuSVuICHoLuRJHPElmzqMS8wzjz749Lc/JpGd8/ivPy4BInA/c\nmFUVnxfQbHDFQXSLf4L+bCjXnsybJ2udxPzQr0+GGdNJJhI6wNJw1OIqqL+2s5zqf16rvvWZHApO\n0H40FtqpOpdbhvTWBns=";
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("client_id", client_id);
		param.put("client_secret", client_secret);
		param.put("grant_type", "refresh_token");
		param.put("refresh_token", refreshToken);
		httpReq(access_token_url, methodType, param);
	}
	
	@Test
	public void test检查令牌(){
		MJsonObject jsonObj = getAsTokenJson();
		String accessToken = jsonObj.getJsonValue("access_token");
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("access_token", accessToken);
		String url = server_addr + "/checkAccessToken";
		httpReq(url, methodType, param);
	}
	
	@Test
	public void test获取用户信息(){
		MJsonObject jsonObj = getAsTokenJson();
		Map<String, String> param = new HashMap<String, String>();
		String token = jsonObj.getJsonValue("access_token");
		token = "a9WujglSG04WlZqCQ52m2accSS5tOVlPOGi1eObZtMm9sTNkf8k224D2rITtbVKCK6\\/rIr8tByZg\n90bbOgxHKenKh2SwNKdmPc6xMBxi4CsLSiodIKWkY71vJa0tGkNYb2+3W61uJ2Jg5pw6sEZr09eP\n8NxbS3wkJ2vSwErrespMQ86yd4RZ6tk1l1hyyNp4lxe8cWM7P8OInDcJb4h2PmMx+zt95MPMmNke\nhMmLV9bsLr+cnr2laKgbPR6b0isL95vZETo0tA1aoVI28n9mHKpA9o\\/EDST5ci63Ot1hXrZruMDl\nqHOx3UD4hASrNmMVgyPDqdBZzxahnpn5Ows0dA==";
		param.put("access_token", token);
		String res = httpReq(user_info_url, methodType, param);
	}
	
	@Test
	public void test登出(){
		Map<String, String> param = new HashMap<String, String>();
//        response.sendRedirect(logout_url + "?redirect_url=" + redirect_url);
        String res = httpReq(logout_url + "?redirect_url=" + redirect_url, methodType, param);
	}
	
	@Test
	public void test1(){
		
		System.out.println(System.currentTimeMillis());
	}
}
