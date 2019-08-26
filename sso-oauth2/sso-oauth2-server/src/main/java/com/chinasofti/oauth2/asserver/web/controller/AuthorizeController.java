package com.chinasofti.oauth2.asserver.web.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.asserver.Constants;
import com.chinasofti.oauth2.asserver.entity.Client;
import com.chinasofti.oauth2.asserver.entity.PhoneAuth;
import com.chinasofti.oauth2.asserver.exception.CaptchaException;
import com.chinasofti.oauth2.asserver.request.SignSecurityOAuthClient;
import com.chinasofti.oauth2.asserver.request.URLConnectionClient;
import com.chinasofti.oauth2.asserver.securitycheck.Identifier;
import com.chinasofti.oauth2.asserver.securitycheck.SecurityCheck;
import com.chinasofti.oauth2.asserver.service.ClientService;
import com.chinasofti.oauth2.asserver.service.OAuthService;
import com.chinasofti.oauth2.asserver.service.PhoneService;
import com.chinasofti.oauth2.asserver.service.UserService;
import com.chinasofti.oauth2.asserver.shiro.UsernamePasswordCaptchaToken;
import com.chinasofti.oauth2.common.utils.JSONUtils;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthResourceResponse;

@SuppressWarnings({"unchecked","rawtypes","unused"})
@Controller
public class AuthorizeController {
    private static Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

    @Value("#{jediscfg['default_rds_session']}")
    private int defaultRdsSession;

    @Value("#{jediscfg['default_rds_cookie']}")
    private int defaultRdsCookie;

    @Value("#{cfg['cdn.resourceUrl']}")
    private String cdnUrl;

    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private ClientService clientService;
    @Autowired
    UserService userService;
    @Autowired
    private SecurityCheck clientIdSecurityCheck;

    @Autowired
    PhoneService phoneService;

    @RequestMapping("/authorize")
	public Object authorize(Model model, HttpServletRequest request,
			HttpServletResponse response) throws URISyntaxException,
			OAuthSystemException {
    	long startTime = System.currentTimeMillis();
        String phoneId = request.getParameter(Constant.HTTP_REQUEST_PARAM_PHONE_ID);
        logger.debug("进入认证鉴权模块========>start");
        Object rtnObj = null;
        if(StringUtils.isEmpty(phoneId)){
            logger.info("进入web端认证鉴权========>start");
            rtnObj = webAuthorize(model,request);
        }else{
            logger.info("进入手机端认证鉴权========>start(phoneId=" + phoneId + ")");
            rtnObj = phoneAuthorize(model,request);
        }
        
        long endTime = System.currentTimeMillis();
        logger.info("======授权接口总耗时：" + (endTime-startTime) + "ms");
        return rtnObj;
    }


    private Object webAuthorize(Model model,
                                HttpServletRequest request) throws URISyntaxException, OAuthSystemException{

        try {
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

            //检查传入的客户端id是否正确
            Identifier identifier = new Identifier(clientIdSecurityCheck);
            if(identifier.verificationTime(request)){//判断是否是过期
                logger.info("clientIdSecurityCheck==========>该访问由于client_id频繁错误已被禁止，未达到解禁时间");
//                OAuthResponse response =
//                        com.chinasofti.oauth2.asserver.response.OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.TOO_MANY_CONNECTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));

                return webAuthorizeFail(request,Constants.CLIENT_TOO_MANY_CONNECTION);
            }
            String clientId = oauthRequest.getClientId();
			if (!oAuthService.checkClientId(clientId)) {
                logger.info("checkClientId==========>无效的client_id:" + clientId);
                identifier.isReplyToken(request);
//                OAuthResponse response =
//                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                return webAuthorizeFail(request,Constants.INVALID_CLIENT_DESCRIPTION);
            }else{
                identifier.removeKey(request);
            }

            //现在是从session中取得用户信息的，改为从cookie中获取，用于单点登录
            Subject subject = SecurityUtils.getSubject();
            //如果用户没有登录，跳转到登陆页面
            if(!subject.isRemembered()){
                logger.info("用户未使用cookie记住密码功能=======>");
                if(!subject.isAuthenticated()) {
                    logger.info("用户未登陆=======>");
                    if(!webLogin(subject, request)) {
                    	//对于自己定义的登录页面，失败时返回自己的登录页
                    	String loginURL = oauthRequest.getParam("login_url");
                    	if(loginURL != null && !"".equals(loginURL)) {
                    		ResponseEntity resEntity = returnLogin(request, loginURL);
                    		if(resEntity != null) {
                    			return resEntity;
                    		}
                    	}
                    	//登录失败时跳转到登陆页面
                          logger.info("跳转到登陆界面=======>");
//                        String asRedirectUrl = request.getRequestURL()+"?"+request.getQueryString();
//                        String asRedirectUrlBase64 = Base64.encodeToString(asRedirectUrl.getBytes());
//                        model.addAttribute("asRedirectUrl",asRedirectUrlBase64);
                        Client client = clientService.findByClientId(clientId);
                        model.addAttribute("client", client);
                        model.addAttribute("cdnUrl",cdnUrl+"/"+client.getClientId());

                        //通过clientId,查询应用的自定义登录界面,若没有,则跳转到默认登录页面
                        Client clientLogin = clientService.findOneByClientId(client.getClientId());
                        String loginFilePath = clientLogin.getLoginFilePath();
                        if(StringUtils.isEmpty(loginFilePath)){ //没有设定登录页面
                            return "oauth2login";
                        }else{
                            //自定义登录页
                            if(!loginFilePath.startsWith("/")){
                                loginFilePath = "/"+loginFilePath;
                            }
                            if(loginFilePath.contains(".jsp")){
                                loginFilePath = loginFilePath.replace(".jsp", "");
                            }
                            return loginFilePath;
                        }

                    }
                }
            }


            String username = (String)subject.getPrincipal();
            //TODO 判读用户和应用之间的权限关系-张佳星
            logger.info("判断用户与应用权限=====>start");
            String userId = userService.findByUsername(username).getUserId();
            if (!oAuthService.isHasAppAccess(clientId,userId)) {
                logger.info("判断用户与应用权限=====>用户对该应用没有访问权限");
                //将之前登录信息清空
                subject.logout();
//                OAuthResponse response =
//                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                return webAuthorizeFail(request,Constants.APPLICATION_ACCESS_DENIED);
            }
            logger.debug("判断用户与应用权限(pass)=====>end");
            //生成授权码
            String authorizationCode = null;

            //TODO 除了code还要支持token
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthService.addAsCode(authorizationCode,clientId,userId);
//                oAuthService.addAuthCode(authorizationCode, username);
            }
            logger.info("生成授权码=====>authorizationCode:" + authorizationCode);
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            //得到到客户端重定向地址
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            String targetURL = oauthRequest.getParam("target_url");
            builder.setParam("target_url", targetURL);
            
            //暂时不写日志
//            this.saveRemoteLog(username, request.getRemoteAddr());

            //构建响应
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
            
            if(redirectURI != null && redirectURI.startsWith("json")) {
            	logger.info("web版认证鉴权===>end，不进行重定向");
            	return new ResponseEntity(authorizationCode, HttpStatus.OK);
            }

            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers = new HttpHeaders();
            String locationUri = response.getLocationUri();
            //设置随机数
//            if(locationUri.contains("?")) {
//            	locationUri += "&randomcode=" + System.currentTimeMillis();
//            } else {
//            	locationUri += "?randomcode=" + System.currentTimeMillis();
//            }
			headers.setLocation(new URI(locationUri));
            //设置Cache-Control
//            headers.setCacheControl("no-store");
            logger.info("web端认证鉴权===>end " + redirectURI);
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.error("认证鉴权出错=====>",e);
            //出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                //告诉客户端没有传入redirectUri直接报错
                return new ResponseEntity("OAuth callback url needs to be provided by client!!!", HttpStatus.NOT_FOUND);
            }

            //返回错误消息（如?error=）
            final OAuthResponse response =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }

    }
    
    private ResponseEntity returnLogin(HttpServletRequest request, String loginURL) {
    	//进行OAuth响应构建
        try {
			OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
			        OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
			builder.setParam("login_url", loginURL);

			//构建响应
			final OAuthResponse response = builder.location(loginURL).buildQueryMessage();
			
			//根据OAuthResponse返回ResponseEntity响应
			HttpHeaders headers = new HttpHeaders();
			String locationUri = response.getLocationUri();
			headers.setLocation(new URI(locationUri));
			return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    private Object phoneAuthorize(Model model,
                                  HttpServletRequest request) throws URISyntaxException, OAuthSystemException{

        try {
            //构建OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            String phoneId = oauthRequest.getParam(Constant.HTTP_REQUEST_PARAM_PHONE_ID);

            //检查传入的客户端id是否正确
            Identifier identifier = new Identifier(clientIdSecurityCheck);
            if(identifier.verificationTime(request)){//判断是否是过期
                logger.info("clientIdSecurityCheck==========>该访问由于client_id频繁错误已被禁止，未达到解禁时间");
//                OAuthResponse response =
//                        com.chinasofti.oauth2.asserver.response.OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.CLIENT_TOO_MANY_CONNECTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                return phoneAuthorizeFail(request, Constants.CLIENT_TOO_MANY_CONNECTION);
            }
            String clientId = oauthRequest.getClientId();
			if (!oAuthService.checkClientId(clientId)) {
                logger.info("checkClientId==========>无效的client_id:" + clientId);
                identifier.isReplyToken(request);
//                OAuthResponse response =
//                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                return phoneAuthorizeFail(request, Constants.INVALID_CLIENT_DESCRIPTION);
            }else{
                identifier.removeKey(request);
            }

            //手机端单点，讲认证信息写入缓存，用于单点登录

            //1.判断缓存中是否存在用户信息
            Subject subject = SecurityUtils.getSubject();

            PhoneAuth phoneAuth = phoneService.findPhoneAuth(phoneId);
            if(!isPhoneRemember(subject, request, phoneAuth)){
                logger.info("用户未使用记住密码功能=======>");
                if(!phoneLogin(subject, request, phoneAuth)) {//登录失败时跳转到登陆页面
                    logger.info("跳转到登陆界面=======>");
                    Client client = clientService.findByClientId(clientId);
                    model.addAttribute("client", client);
                    model.addAttribute("cdnUrl",cdnUrl+"/"+client.getClientId());
                    return "oauth2login";
                }
            }

            String username = (String)subject.getPrincipal();

            //手机获取授权码，无状态，验证完login即可logout
            subject.logout();

            logger.info("判断用户与应用权限=====>start");
            String userId = userService.findByUsername(username).getUserId();
            if (!oAuthService.isHasAppAccess(clientId,userId)) {
                logger.info("判断用户与应用权限=====>用户对该应用没有访问权限");
                //将之前登录信息清空
//                OAuthResponse response =
//                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
//                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
//                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
//                                .buildJSONMessage();
//                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                //失败时清除缓存
                phoneService.removePhoneAuth(phoneId);
                return phoneAuthorizeFail(request, Constants.APPLICATION_ACCESS_DENIED);
            }
            logger.info("判断用户与应用权限(pass)=====>end");
            //生成授权码
            String authorizationCode = null;

            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthService.addAsCode(authorizationCode,clientId,userId);
//                oAuthService.addAuthCode(authorizationCode, username);
            }
            logger.info("生成授权码=====>authorizationCode:" + authorizationCode);
            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            //得到到客户端重定向地址
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            
            this.saveRemoteLog(username, request.getRemoteAddr());
            
            //构建响应
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

            //桌面版
            if(redirectURI != null && redirectURI.startsWith("json")) {
            	logger.info("桌面版认证鉴权===>end");
            	return new ResponseEntity(authorizationCode, HttpStatus.OK);
            }
            
            //根据OAuthResponse返回ResponseEntity响应
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            logger.info("手机端认证鉴权===>end");
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            logger.error("认证鉴权出错=====>",e);
            //出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                //告诉客户端没有传入redirectUri直接报错
                return new ResponseEntity("OAuth callback url needs to be provided by client!!!", HttpStatus.NOT_FOUND);
            }

            //返回错误消息（如?error=）
            final OAuthResponse response =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }

    }

    private boolean webLogin(Subject subject, HttpServletRequest request) {
        if("get".equalsIgnoreCase(request.getMethod())) {
            logger.info("该请求为get请求，不属于登陆操作=======>");
            return false;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        String available = request.getParameter("available");//0表示不校验验证码
        boolean rememberMe = Boolean.valueOf(request.getParameter("rememberMe"));
        logger.info("执行登陆操作=======>(username:" + username + ",captcha:" + captcha + ",rememberMe:" + rememberMe+")");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(username, password.toCharArray(),rememberMe,null,captcha);
        //去掉校验验证码逻辑
        if("0".equals(available)) {
        	token.setIsAvailable(false);
        }

        try {
            subject.login(token);
            return true;
        } catch (UnknownAccountException uae){
            logger.error("用户不存在",uae);
            request.setAttribute("error","用户名或密码错误");//账号不存在
            return false;
        } catch (IncorrectCredentialsException ice){
            logger.error("密码不正确",ice);
            request.setAttribute("error","用户名或密码错误");//密码错误
            return false;
        } catch (CaptchaException ce){
            logger.error("验证码不正确",ce);
            request.setAttribute("error","验证码错误");
            return false;
        } catch (ExcessiveAttemptsException eae){
            logger.error("密码输错5次",eae);
            request.setAttribute("error","密码错误5次");
            return false;
        } catch (Exception e) {
            logger.error("未知错误",e);
//            request.setAttribute("error", "登录失败:" + e.getClass().getName());
            request.setAttribute("error", "未知错误");
            return false;
        }
    }

    private boolean phoneLogin(Subject subject, HttpServletRequest request, PhoneAuth phoneAuth) {
        if("get".equalsIgnoreCase(request.getMethod())) {
            logger.info("该请求为get请求，不属于登陆操作=======>");
            return false;
        }
        String phoneId = "";
        String username = "";
        String password = "";
        String captcha = "";
        boolean rememberMe = false;

        phoneId = request.getParameter(Constant.HTTP_REQUEST_PARAM_PHONE_ID);
        username = request.getParameter("username");
        password = request.getParameter("password");
        captcha = request.getParameter("captcha");
        String available = request.getParameter("available");//0表示不校验验证码
        rememberMe = Boolean.valueOf(request.getParameter("rememberMe"));
        logger.info("执行登陆操作=======>(username:" + username + ",captcha:" + captcha + ",rememberMe:" + rememberMe+")");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(username, password.toCharArray(),Boolean.FALSE,null,captcha);
        //去掉校验验证码逻辑
        if("0".equals(available)) {
        	token.setIsAvailable(false);
        }

        try {
            subject.login(token);

            //讲信息添加到缓存中
            phoneAuth = new PhoneAuth();
            phoneAuth.setPhoneId(phoneId);
            phoneAuth.setUsername(username);
            phoneAuth.setPassword(password);
            phoneAuth.setRememberMe(rememberMe);
            if(rememberMe){
                phoneService.addPhoneAuth(phoneId,phoneAuth,defaultRdsCookie);
            }else{
                phoneService.addPhoneAuth(phoneId,phoneAuth,defaultRdsSession);
            }

            return true;
        } catch (UnknownAccountException uae){
            logger.error("用户不存在",uae);
            request.setAttribute("error","用户名或密码错误");//账号不存在
            return false;
        } catch (IncorrectCredentialsException ice){
            logger.error("密码不正确",ice);
            request.setAttribute("error","用户名或密码错误");//密码错误
            return false;
        } catch (CaptchaException ce){
            logger.error("验证码不正确",ce);
            request.setAttribute("error","验证码错误");
            return false;
        } catch (ExcessiveAttemptsException eae){
            logger.error("密码输错5次",eae);
            request.setAttribute("error","密码错误5次");
            return false;
        } catch (Exception e) {
            logger.error("未知错误",e);
//            request.setAttribute("error", "登录失败:" + e.getClass().getName());
            request.setAttribute("error","未知错误");
            return false;
        }

    }

    private boolean isPhoneRemember(Subject subject, HttpServletRequest request, PhoneAuth phoneAuth) {
        String username = "";
        String password = "";
        boolean rememberMe = false;

        if(phoneAuth!=null){
            username = phoneAuth.getUsername();
            password = phoneAuth.getPassword();
            rememberMe = phoneAuth.isRememberMe();

            if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return false;
            }

            UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(username, password.toCharArray(),Boolean.FALSE,null,false);

            try {
                subject.login(token);
                //刷新rds
                if(rememberMe){
                    phoneService.refreshExpire(phoneAuth.getPhoneId(),defaultRdsCookie);
                }else{
                    phoneService.refreshExpire(phoneAuth.getPhoneId(),defaultRdsSession);
                }


                return true;
            } catch (Exception e) {
                request.setAttribute("error", "登录失败:" + e.getClass().getName());
                return false;
            }
        }else{
            return false;
        }

    }

    private String webAuthorizeFail(HttpServletRequest request,String msg){
        request.setAttribute("error",msg);
        return "oauth2login";

    }

    private String phoneAuthorizeFail(HttpServletRequest request,String msg){
        request.setAttribute("error",msg);
        return "oauth2login"; //"oauth2PhoneLogin";

    }
    
    @Value("#{cfg['rs.logUrl']}")
    private String logUrl;
    
    private void saveRemoteLog(String username,String ip) {
    	SignSecurityOAuthClient oAuthClient=new SignSecurityOAuthClient(new URLConnectionClient());
        OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("logUrl", logUrl, com.chinasofti.oauth2.common.OAuth.HttpMethod.POST);
        Map<String, Object> valueMap = new HashMap<String,Object>();
        valueMap.put("userName", username);
        valueMap.put("ipAddress", ip);
        valueMap.put("comment", "单点登录");
        String logBeanJson = "";
        //todo 这里如果需要记录其他信息的话，要到数据库查询，目前只能给出用户名称和IP地址

        try {
            logBeanJson = JSONUtils.buildJSON(valueMap);
            userInfoRequest.setParameter("logBeanJson",logBeanJson);
            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}