/**
 * 
 */
package com.chinasofti.oauth2.client.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinasofti.oauth2.client.model.OAuth2Config;
import com.chinasofti.oauth2.client.CSIClient;
import com.chinasofti.oauth2.client.util.CSiUtil;
import com.chinasofti.oauth2.client.util.Constant;
import com.chinasofti.oauth2.client.util.IOUtils;
import com.chinasofti.oauth2.client.util.StringUtils;
import com.chinasofti.oauth2.exception.GetAccessTokenException;
import com.chinasofti.oauth2.exception.GetUserInfoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


/**
 * 基于OAuth2协议实现单点登陆功能，使用配置方法：
 * <pre>
 * 1.在web.xml中注册此Filter,对需要获取到当前用户的url进行拦截，注意:filter-mapping的拦截要在转码Filter的后面，其他Filter的前面
 * 2.编辑WEB-INF/class/sso.properties文件(或通过增加Filter参数configFileName指定配置文件名称),内容如下：
	#应用ID
	client_id=TEST-APP
	#应用凭证
	client_secret=8d42ee5e-d1ab-4d60-abf0-499457a11579
	#应用注册的回调地址
	redirect_url=http://localhost:8080/R1AuthricationServer/client.jsp
	#授权请求的URL
	authorize_url=http://localhost:8080/R1AuthricationServer/oauth2/authorize
	#获取令牌的URL
	access_token_url=http://localhost:8080/R1AuthricationServer/oauth2/access_token
	#获取用户信息的API调用地址
	api_url=http://localhost:8080/R1AuthricationServer/api/user
 *3.获取当前用户的方式：
 *	1)通过request当前用户id：request.getRemoterUser()
 *	2)当前用户id：SSO.getUserId()
 *	3)当前用户JSONObject对象：SSO.getUser()
 * </pre>
 * @author yangkai
 *
 */
public class CSIFilter implements Filter{
	private OAuth2Config config;
	private static final String defaultConfigFileName="OAuth2-config.properties";
	private static Logger logger=LoggerFactory.getLogger(CSIFilter.class);
	private static String configFileName=defaultConfigFileName;
	private static String EXCEPT_PATTERNS="";
	private static String EXCEPT_PARAMS;
	private static String serviceString="serviceAuthenticate";
	private static String csiString = "CSIAuthenticate";
	private static Gson gson;
	public static String getConfigFileName() {
		return configFileName;
	}
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CSIClient client=new CSIClient(config,(HttpServletRequest)request,(HttpServletResponse)response);
		if(logger.isDebugEnabled()){
			logger.debug("CSIFilter session hashcode:"+((HttpServletRequest)request).getSession());
		}
		try{
			String requestURI = ((HttpServletRequest)request).getRequestURI();
			if(client.isLogin((HttpServletRequest)request)){
				logger.info("CSIFilter 进入登录方法isLogin()----url:"+requestURI);
				chain.doFilter(client.request(), response);
				logger.info("CSIFilter isLogin方法通过");
				return;
			}else if(client.isExcluded(EXCEPT_PATTERNS, EXCEPT_PARAMS, (HttpServletRequest)request)){
				logger.info("CSIFilter url匹配----url:"+requestURI);
				logger.debug("CSIFilter isExcluded");
				chain.doFilter(request, response);
				logger.info("CSIFilter url匹配成功通过");
				return;
			}else if("zmitiApp".equals(((HttpServletRequest)request).getHeader("X-Requested-By"))){
				//防止做ajax请求的静态页面登录超时僵死在那
	    		PrintWriter out = response.getWriter();
	    		out.print("{\"status\":302,\"msg\":\"登录超时！请重新登录\"}");
	    		out.flush();
	    		out.close();
	    		chain.doFilter(request, response);
				logger.info("操作平台ajax请求,session 过期");
				return;
	    	}else if(client.isCallback()){
				logger.info("CSIFilter 验证是否服务端返回的地址----url:"+requestURI);
				logger.debug("CSIFilter isCallback");
				chain.doFilter(request, response);
				logger.info("CSIFilter 验证是否服务端返回的地址，成功通过");
				return;
			}
			String code=client.getAuthrizationCode();
			logger.info("CSIFilter code:"+code);
			if(code==null){
				logger.info("CSIFilter code为空时 从服务端获取code----url:" + requestURI);
//				client.toGetAuthrizationCode();
				client.toGetAuthrizationCode((HttpServletRequest)request);
				return;
			}
			
			if (!client.isValidState((HttpServletRequest) request)) {
				logger.info("CSIFilter 验证state失败");
				String result = returnResourceFile(Constant.TEMPLATE_NAME, Constant.STATE_ERROR);
				response.getWriter().write(result);
				return;
			}
			logger.info("CSIFilter 去调用用户信息接口方法----url:"+requestURI);
			client.bindUserInfo(code);

//				if(!client.toOriginalURL()){//如果直接敲有code的url地址
//					chain.doFilter(client.request(), response);
//				}
			if(client.prefect()){
				return;
			}


			if(client.toTargetURL()) {
				return;
			}
			logger.info("====CSIFilter执行完毕，接着执行其他的filter！");
			
			chain.doFilter(request, response);
		}catch (GetAccessTokenException gte){
			logger.error("客户端拦截器获取令牌失败===========>",gte);
			String result = returnResourceFile(Constant.TEMPLATE_NAME, Constant.GET_ACCESSTOKEN_ERROR + "," + gte.getMessage());
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(result);
		}catch (GetUserInfoException gue){
			logger.error("客户端拦截器获取用户信息失败===========>",gue);
			String result = returnResourceFile(Constant.TEMPLATE_NAME, Constant.GET_USERINFO_ERROR);
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(result);
		}catch(Exception e){
			logger.error("客户端拦截器执行异常===========>",e);
//			throw new ServletException(e.getMessage());
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write("执行异常，请联系管理员");
		}
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			configFileName=filterConfig.getInitParameter("configFileName");
			if(configFileName==null || configFileName.equals("")){
				configFileName=defaultConfigFileName;
			}
			config=new OAuth2Config(configFileName);
			CSiUtil.config = config;// 放到全局变量里面
			EXCEPT_PATTERNS=filterConfig.getInitParameter("_except_urlpattern");
			EXCEPT_PARAMS=filterConfig.getInitParameter("_except_param");
			if(gson==null)gson=new Gson();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private String returnResourceFile(String fileName,String info) throws ServletException, IOException {
		String text = IOUtils.readFromResource("support/http/resources/" + fileName);
		if(text == null) {
			return "错误信息模版为定义";
		} else {
			return org.apache.commons.lang.StringUtils.replace(text, Constant.TEMPLATE_REPLACE_STRING, info);
		}

	}

}
