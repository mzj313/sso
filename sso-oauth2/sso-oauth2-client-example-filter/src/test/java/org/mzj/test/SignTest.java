package org.mzj.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.chinasofti.oauth2.utils.MD5;

public class SignTest {
	private static final String secret = "chinasoftipdmi"; //加密串
	
	@Test
	public void testSign1(){
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("b", "2");
		parameters.put("a", "1");
		parameters.put("c", "3");
		getSign(parameters);
	}
	
	@Test
	public void testSign2(){
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("access_token", "111111");
		parameters.put("clientId", "f5a1611335a54c3abea0964202a26cd2");
		parameters.put("type", "1");
		parameters.put("userId", "451b55646f434019b1d176f56b75512a");
		getSign(parameters);
	}

	private void getSign(Map<String, String> parameters) {
		String sort = toString(parameters);
		System.out.println(sort);
		System.out.println("sign=" + createSignature(sort));
	}

	public static String toString(Map<String,String> parameters){
		// 将加入两个id的key集合排序
        Set<String> keySet = parameters.keySet();
        List<String> paramkeys = new ArrayList<String>(keySet);
        Collections.sort(paramkeys);

        // 生成一个新的map将两个id放进去
        Map<String, Object> newParams = new HashMap<String, Object>(parameters);

        // 根据新生成的map和排序后的key集合生成访问字符串并urlencode
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : paramkeys) {
            String value = newParams.get(key).toString();
            if (value != null && !"".equals(value)) {
                paramBuilder.append(key).append("=").append(value).append("&");
            }
        }
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        String param = paramBuilder.toString();
        
        String encode = null;
		try {
			encode = java.net.URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		paramBuilder = new StringBuilder(encode);
        return paramBuilder.toString();
	}
	
	public static String createSignature(String query){
        StringBuffer keys = new StringBuffer();
        keys = keys.append(secret).append(query).append(secret);
        System.out.println(keys);
        return MD5.getEncryptResult(keys.toString()).toLowerCase();
    }
	
	@Test
	public void testPassword() {
		String password = "test";
		String salt = "test";
		System.out.println(MD5.getEncryptResult(password + "{" + salt + "}"));
	}
}
