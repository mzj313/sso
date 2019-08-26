package org.mzj.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chinasofti.oauth2.client.model.MJsonObject;
import com.google.gson.JsonParser;

public class APIAuthDesktopVersionTest extends TestBase {
	
	private String server_addr = authorize_url.substring(0,
			authorize_url.indexOf("/pup-asserver"))
			+ "/pup-asserver";
	private String phone_id = "001";
	
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
		param.put("redirect_uri", redirect_url);//json开头
		param.put("state", "xxx");
		param.put("phone_id", phone_id);
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
		String ascode = getAsCode();
		Map<String, String> param = new HashMap<String, String>();
		param.put("client_id", client_id);
		param.put("client_secret", client_secret);
		param.put("grant_type", "authorization_code");
		param.put("redirect_uri", redirect_url);//json开头
		param.put("code", ascode);
		String res = httpReq(access_token_url, methodType, param);
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
		param.put("access_token", jsonObj.getJsonValue("access_token"));
		String res = httpReq(user_info_url, methodType, param);
	}
	
	@Test
	public void test登出(){
//		phone_id = "bb1ca5fb30fb4a6c1c00a8acce966eb7";
		Map<String, String> param = new HashMap<String, String>();
		param.put("phone_id", phone_id);
		httpReq(server_addr + "/app/logout", methodType, param);
	}
}
