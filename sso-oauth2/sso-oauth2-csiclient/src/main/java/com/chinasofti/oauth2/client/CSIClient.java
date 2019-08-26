/**
 * 
 */
package com.chinasofti.oauth2.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chinasofti.oauth2.client.model.CSIClientCertification;
import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.chinasofti.oauth2.client.util.CSiUtil;
import com.chinasofti.oauth2.client.util.Constant;
import com.chinasofti.oauth2.client.util.StateGenerator;
import com.chinasofti.oauth2.client.util.StringUtils;
import com.chinasofti.oauth2.client.util.UrlExpProcessor;
import com.chinasofti.oauth2.common.OAuth;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.message.types.GrantType;
import com.chinasofti.oauth2.exception.GetAccessTokenException;
import com.chinasofti.oauth2.exception.GetUserInfoException;
import com.chinasofti.oauth2.exception.OAuth2Exception;
import com.chinasofti.oauth2.request.OAuthClientRequest;
import com.chinasofti.oauth2.request.OAuthClientRequest.TokenRequestBuilder;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthAccessTokenResponse;
import com.chinasofti.oauth2.response.OAuthResourceResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import sun.misc.BASE64Encoder;


/**
 * @author yangkai
 *
 */
public class CSIClient {


	private static UrlExpProcessor urlExpProcessor;
	private HttpServletResponse response;
	private HttpServletRequest request;
	private OAuth2Config config;
	private JsonObject person;
	private String url;

	private static Logger logger=LoggerFactory.getLogger(CSIClient.class);
	private JsonParser parser;

	private SignSecurityOAuthClient oAuthClient;

	public CSIClient(OAuth2Config config, HttpServletRequest request2,
					 HttpServletResponse response2) {
		this.config=config;
		this.request=request2;
		this.response=response2;
		this.url=request.getRequestURL().toString();
		this.parser = new JsonParser();

		this.oAuthClient = new SignSecurityOAuthClient(new URLConnectionClient());
	}

	/**
	 * 获取授权码
	 * @return
	 */
	public String getAuthrizationCode() {
//		logger.info("csiClient 进入getAuthrizationCode");
		String code=request.getParameter("code");
		boolean isRedirectURL = isRedirectURL();
		logger.info("csiClient 进入getAuthrizationCode，code="+code + "，isRedirectURL=" + isRedirectURL);
		if(!isRedirectURL){
//			return null;
		}
		return code;
	}

	/**
	 * 获取phone_id
	 * @return
	 */
	public String getPhoneId() {
//		logger.info("csiClient getPhoneId");
		String phoneId=request.getParameter("phone_id");
		logger.info("csiClient getPhoneId，phone_id="+phoneId);
		return phoneId;
	}

	private boolean isRedirectURL() {
		logger.info("isRedirectURL()  url=" + url + ", config.redirecturl=" + config.getRedirectURL());
		if(url.equals(config.getRedirectURL())){
			return true;
		}else if(!config.getRedirectURL().endsWith("/")){
			return url.equals(config.getRedirectURL()+"/");
		}
		return false;
	}


	/**
	 * 引导用户授权
	 * @throws IOException 
	 */
	public void toGetAuthrizationCode() throws IOException {
		toGetAuthrizationCode(null);
	}

	/**
	 * 引导用户授权
	 * @throws IOException 
	 */
	public void toGetAuthrizationCode(HttpServletRequest request) throws IOException {
		logger.info("csiClient 进入toGetAuthrizationCode");
		String phoneId = getPhoneId();
//		StringBuffer originalURL=new StringBuffer(url);
//		String quering=getParamUrl(request,request.getCharacterEncoding(),null);
//		if(quering.length()>0){
//			originalURL.append("?").append(quering);
//		}
//		request.getSession().setAttribute(Constant.ORIGINALURL, originalURL.toString());
		StringBuilder url=new StringBuilder(config.getAuthorizeURL());
		String m=config.getAuthorizeURL().contains("?") ? "&" : "?";
		url.append(m);
		if(org.apache.commons.lang.StringUtils.isNotBlank(phoneId)){
			url.append("phone_id=").append(URLEncoder.encode(phoneId,"utf-8")).append("&");
		}
		url.append("client_id=").append(URLEncoder.encode(config.getClientId(), "utf-8"));
		url.append("&redirect_uri=").append(URLEncoder.encode(config.getRedirectURL(), "utf-8"));
		url.append("&response_type=code");
		url.append("&state=").append(URLEncoder.encode(getState(request), "utf-8"));
		//目标回调地址
		if(request != null) {
			String targetURL = request.getRequestURL().toString();
			if(targetURL != null) {
				StringBuffer targetURLBuf = new StringBuffer();
				Enumeration<String> parameterNames = request.getParameterNames();
				if(parameterNames.hasMoreElements()) {
					if(targetURL.endsWith("/")) {
						targetURL = targetURL.substring(0, targetURL.length()-1) + "?";
					} else {
						targetURL = targetURL + "?";
					}
				}
				targetURLBuf.append(targetURL);
				while(parameterNames.hasMoreElements()) {
					String key = parameterNames.nextElement();
					URLEncoder.encode(request.getParameter(key),"utf-8");
					targetURLBuf.append(key).append("=").append(request.getParameter(key));
					if(parameterNames.hasMoreElements()) {
						targetURLBuf.append("&");
					}
				}
				url.append("&target_url=").append(URLEncoder.encode(targetURLBuf.toString(),"utf-8"));
			}
		}
		String _url = url.toString();
		response.sendRedirect(_url);
		logger.info("csiClient 退出toGetAuthrizationCode，url=" + _url);
	}


	/**
	 * 调用API获取用户信息并将用户信息绑定到会话中
	 * @param authrizationCode
	 * @return
	 * @throws OAuth2Exception 
	 * @throws Exception 
	 */
	public String bindUserInfo(String authrizationCode) throws OAuth2Exception {
		logger.info("csiclient 去调用用户信息接口方法");
		String accessToken = "";
		String refreshToken = "";
		String expiration = "";
		try {
			TokenRequestBuilder builder = OAuthClientRequest
				.tokenLocation(config.getAccessTokenURL())
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.setClientId(config.getClientId())
				.setClientSecret(config.getClientSecret())
				.setCode(authrizationCode)
				.setRedirectURI(config.getRedirectURL());
			OAuthClientRequest accessTokenRequest = builder.buildQueryMessage();

			logger.info("======csiclient 去调用access_token_url接口，参数：" + builder);
			OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
			accessToken = oAuthResponse.getAccessToken();
			refreshToken = oAuthResponse.getRefreshToken();
			expiration = oAuthResponse.getExpiration();
		} catch (OAuthProblemException e) {
			logger.error("获取令牌信息错误=======>",e);
			throw new GetAccessTokenException(e.getDescription());
		} catch (Exception e) {
			logger.error("获取令牌信息错误=======>",e);
			throw new GetAccessTokenException(e);
		}
		String openId = "";
		String userId = "";
		String personStr;
		try{
			logger.info("======csiclient 去调用user_info_url接口，参数：" + "accessToken=" + accessToken);
			OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("user_info", config.getProperty("user_info_url"), OAuth.HttpMethod.GET);
			userInfoRequest.setAccessToken(accessToken);
			OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
			personStr = resourceResponse.getBody();
			logger.info("======返回的user_info:" + personStr);
			person = parser.parse(personStr).getAsJsonObject();


			logger.info("csiclient 去调用用户信息接口方法  person:" + person);

			openId = this.getJsonValue(person, "openId");
			userId = this.getJsonValue(person, "userId");

		} catch (Exception e) {
			logger.error("获取用户基本信息错误=======>", e);
			throw new GetUserInfoException(e);
		}
		//检验email  and  tel   ----------start
		try{
			logger.info("======获取用户的邮箱 和 电话");
			OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("user_info", config.getProperty("asserver_url")+"/emailTel", OAuth.HttpMethod.GET);
			userInfoRequest.setParameter("userId",userId);
			OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
			String personStr2 = resourceResponse.getBody();
			logger.info("======返回的user_info:" + personStr2);
			JsonObject person2 = parser.parse(personStr2).getAsJsonObject();


			logger.info("csiclient 去调用用户  email  和 tel接口方法  person2:" + person2);

			String email = this.getJsonValue(person2, "email");
			String tel = this.getJsonValue(person2, "tel");

			if(StringUtils.isEmpty(email) && StringUtils.isEmpty(tel)){
				request.setAttribute("prefect", config.getProperty("asserver_url")+"/prefect?userId="+userId+"&&targetURL="+URLEncoder.encode(request.getParameter("target_url"),"utf-8"));
				return null;
			}

		} catch (Exception e) {
			logger.error("获取用户邮箱 电话 错误=======>",e);
			throw new GetUserInfoException(e);
		}
		//检验 email  and  tel   ------------end






//		request.getSession().setAttribute(Constant.CSIUSER, new MJsonObject(person));
		request.getSession().setAttribute(Constant.CSIUSER, personStr);
		request.getSession().setAttribute(Constant.CSIUSERID, userId);
		request.getSession().setAttribute(Constant.CSIOPENID, openId);
		request.getSession().setAttribute(Constant.SESSIONAUTHENTICATIONKEY, new CSIClientCertification(userId, accessToken, refreshToken, expiration));
		CSiUtil.setSession(request.getSession());

		logger.info("当前登录用户 userId:"+CSiUtil.getCurrentUserId());
		logger.info("登陆后的令牌信息：" + request.getSession().getAttribute(Constant.SESSIONAUTHENTICATIONKEY));
		return person.toString();
	}

	/**
	 * 获取当前用户ID
	 * @return
	 */
	public String getUserId() {
		return (String)request.getSession().getAttribute(Constant.CSIUSERID);
	}

	/**
	 * 是否从OAuth2认证服务器返回的请求，一般情况为出错了
	 * @return
	 */
	public boolean isCallback() {
		if(isRedirectURL()){
			String error=request.getParameter("error");
//			String errorCode=request.getParameter("errorCode");
//			String errorDescription=request.getParameter("errorDescription");
			if(error!=null){
				return true;
			}
		}
		return false;
	}

	public HttpServletRequest request() {
//		SystemUtil.setCurThreadAttribute(CSIUSER, request.getSession().getAttribute(CSIUSER));
//		SystemUtil.setCurThreadAttribute(CSIUSERID, request.getSession().getAttribute(CSIUSERID));
//		SystemUtil.setCurThreadAttribute(CSIAPICLIENT, request.getSession().getAttribute(CSIAPICLIENT));
//		SystemUtil.setCurThreadAttribute(SESSIONAUTHENTICATIONKEY, request.getSession().getAttribute(SESSIONAUTHENTICATIONKEY));
		return new CSIRequestWrapper(request);
	}

	class CSIRequestWrapper extends HttpServletRequestWrapper {
	    public CSIRequestWrapper(HttpServletRequest request) {
	        super(request);
	    }

	    public String getRemoteUser() {
	        return getUserId();
	    }
	}

	private String getParamUrl(HttpServletRequest request, String encoding, List exceptPramList){
        Enumeration paramNames = request.getParameterNames();
        StringBuffer query = new StringBuffer();
        try {
            while (paramNames.hasMoreElements()) {
                String param = (String) paramNames.nextElement();
                if(exceptPramList!=null && exceptPramList.contains(param)){
                    continue;
                }
                String[] values = request.getParameterValues(param);
                for (int i = 0; i < values.length; i++) {
                    query.append('&');
                    try {
                        query.append(URLEncoder.encode(param, encoding));
                    } catch (Exception e) {
                        query.append(URLEncoder.encode(param));
                    }

                    query.append('=');

                    try {
                        query.append(URLEncoder.encode(values[i], encoding));
                    } catch (Exception e) {
                        query.append(URLEncoder.encode(values[i]));
                    }
                }
            }
            if (query.length() > 0) {
                query.replace(0, 1, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query.toString();
    }

	public boolean toOriginalURL() throws Exception {
		String originalURL=(String) request.getSession().getAttribute(Constant.ORIGINALURL);
		logger.debug("SSOClient toOriginalURL:"+originalURL);
		if(originalURL!=null){
			response.sendRedirect(originalURL);
			return true;
		}else{
			return false;
		}
	}
	
	public boolean toTargetURL() throws Exception {
		String targetURL = (String) request.getParameter("target_url");
		logger.info("去访问targetURL:" + targetURL);
		if (targetURL != null) {
			response.sendRedirect(targetURL);
			return true;
		}
		return false;
	}

	//新增跳转代码-----------star
	public boolean prefect() throws Exception{
		String prefect = (String) request.getAttribute("prefect");
		if(prefect != null){
			response.sendRedirect(prefect);
			return true;
		}
		return false;
	}
	//新增跳转代码-----------end


	public boolean isExcluded(String patterns) {
		if(urlExpProcessor==null){
			urlExpProcessor=new UrlExpProcessor(patterns);
		}
		boolean result = urlExpProcessor.match(this.url);
		logger.info("进入isExcluded()========>" + urlExpProcessor.getPattern() + ", " + result);
		return result;
	}
	
	public boolean isExcluded(String except_urlpattern, String except_param, HttpServletRequest request) {
		logger.info("进入isExcluded()========>");
		if(except_param != null && !"".equals(except_param)) {
			int idx = except_param.indexOf("=");
			if(idx > 0) {
				String name = except_param.substring(0,idx);
				String value = except_param.substring(idx+1);
				String value_param = request.getParameter(name);
				logger.info("name=" + name + ",value=" + value + ",request:" + value_param);
				if(value.equals(value_param)) {
					return true;
				}
			}
		}
		
		if(urlExpProcessor==null){
			urlExpProcessor=new UrlExpProcessor(except_urlpattern);
		}
		boolean result = urlExpProcessor.match(this.url);
		logger.info(urlExpProcessor.getPattern() + ", " + result);
		return result;
	}

	public boolean isLogin() {
		boolean flag = false;
		Object obj = request.getSession().getAttribute(Constant.SESSIONAUTHENTICATIONKEY);
		if(obj instanceof CSIClientCertification){
			CSIClientCertification my = (CSIClientCertification)obj;
			String userid = my.getUserid();
			logger.info("CSIClient 的isLogin(), userid:" + userid);
			logger.debug("current user:"+userid);
			if(userid!=null && !userid.equals("")){
				flag = true;
			}
		}	
		return flag;
	}
	
	public boolean isLogin(HttpServletRequest req) {
		boolean flag = false;
		Object obj = request.getSession().getAttribute(Constant.SESSIONAUTHENTICATIONKEY);
		logger.info("进入登录验证========> 登陆后的令牌信息：="+obj);
		logger.info("当前登录用户 userId:" + CSiUtil.getCurrentUserId());
		if(obj instanceof CSIClientCertification){
			CSIClientCertification my = (CSIClientCertification)obj;
			String userid = my.getUserid();
			boolean tokenValid = tockenValid(req);
			logger.info("CSIClient 的isLogin(), userid:" + userid+",tokenVlid=:"+tokenValid);
			if(userid!=null && !userid.equals("") && tokenValid){
				CSiUtil.setSession(req.getSession());
				flag = true;
			}
		}
		logger.info("登录验证结果=======>flag:"+flag);
		return flag;
	}
	
	public String[] isCookie()throws Exception{
		Cookie[] cookies = request.getCookies();
		Cookie my = null;
		if (cookies != null) {
			for (Cookie cookie : request.getCookies()) {
				if (Constant.COOKIE_NAME.equals(cookie.getName())) {
					my = cookie;
					break;
				}
			}
			if(my==null)return null;
			byte[] b = my.getValue().getBytes(Constant.charset);
			b=new Base64().decode(b);
			String access = new String(b,Constant.charset);
			String[] tokens = StringUtils.delimitedListToStringArray(access, Constant.COOKIE_SEPERATOR);
			return tokens;
		}else{
			return null;
		}
	}
	
    private boolean tockenValid(HttpServletRequest req){
		Object obj = request.getSession().getAttribute(Constant.SESSIONAUTHENTICATIONKEY);
		if(!(obj instanceof CSIClientCertification)){
			return false;
		}
		CSIClientCertification my = (CSIClientCertification)obj;
    	if(accessTokenValid(my)){
    		return true;
    	}else{
    		try
            {
    			req.getSession(false).invalidate();
            } catch (IllegalStateException e) {
            	logger.debug("tockens invalid,please login again!");
            }
    	}
    	return false;
    }
    
    private String getJsonValue(JsonObject json,String key){
    	JsonElement ele = json.get(key);
    	if(ele==null)return "";
    	return ele.isJsonNull()?"":ele.getAsString();
    }

	/*
	 * 判断访问令牌是否过期
	 * @param accessToken
	 * @return
	 */
	private boolean accessTokenValid(CSIClientCertification my) {

		String expiration = my.getExpiresTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date expirateionDate = null;
		try {
			expirateionDate = sdf.parse(expiration);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(my.getAccessToken()!=null && !my.getAccessToken().equals("") && (expirateionDate.getTime()-System.currentTimeMillis()>0)){
			return true;
		}
		return false;
	}

	/**
	 * 获取state
	 * @param request
	 * @return
	 */
	private String getState(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String state = StateGenerator.genStateCode();
		session.setAttribute(Constant.OAUTH_STATE, state);
		return state;
	}

	/**
	 * 验证state是否相同
	 * @param request
	 * @return
	 */
	public boolean isValidState(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Object so = session.getAttribute(Constant.OAUTH_STATE);
		if (StringUtils.isEmpty(so)) {
			return true;
		} else if (so.toString().equals(request.getParameter(OAuth.OAUTH_STATE))) {
			session.removeAttribute(Constant.OAUTH_STATE);
			return true;
		}
		return false;
	}

}
