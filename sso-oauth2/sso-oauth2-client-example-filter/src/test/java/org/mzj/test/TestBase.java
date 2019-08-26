package org.mzj.test;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chinasofti.oauth2.client.SignSecurityOAuthClient;
import com.chinasofti.oauth2.client.URLConnectionClient;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthResourceResponse;

public class TestBase {
	private static String configFile = "/OAuth2-config.properties";
	private static Properties p = new Properties();
	
	public static String client_id;
	public static String client_secret;
	public static String redirect_url;
	public static String authorize_url;
	public static String access_token_url;
	public static String user_info_url;
	public static String logout_url;

	public static String api_url;
	protected static String methodType = "POST";// GET,PUT
	
	@BeforeClass
	public static void init(){
		try {
			InputStream is = TestBase.class.getResourceAsStream(configFile);
			p.load(is);
		} catch (Exception e) {
			throw new RuntimeException("加载配置文件失败" + e);
		}

		client_id = p.getProperty("client_id");
		client_secret = p.getProperty("client_secret");
		redirect_url = p.getProperty("redirect_url");
		authorize_url = p.getProperty("authorize_url");
		access_token_url = p.getProperty("access_token_url");
		user_info_url = p.getProperty("user_info_url");
		logout_url = p.getProperty("logout_url");
	}
	
	@Test
	public void testConfigFile() {
		System.out.println(p);
	}
	
	public String httpReq(String url, String methodType,
			Map<String, String> param) {
		System.out.println("\n接口url：\n" + url);
		System.out.println("输入参数：\n" + param);
		OAuthResourceResponse resourceResponse = null;
		try {
			SignSecurityOAuthClient oAuthClient = new SignSecurityOAuthClient(
					new URLConnectionClient());

			OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest(
					"org_info", url, methodType);
			if (param != null && param.size() > 0) {
				for (String key : param.keySet()) {
					userInfoRequest.setParameter(key, param.get(key));
				}
			}
			resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = resourceResponse.getBody();
		System.out.println("返回json: \n" + json);
		return json;
	}
}
