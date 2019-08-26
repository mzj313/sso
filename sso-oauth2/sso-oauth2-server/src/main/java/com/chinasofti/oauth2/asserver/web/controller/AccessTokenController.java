package com.chinasofti.oauth2.asserver.web.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.chinasofti.oauth2.Constant;
import com.chinasofti.oauth2.asserver.Constants;
import com.chinasofti.oauth2.asserver.entity.Client;
import com.chinasofti.oauth2.asserver.entity.DefaultExpiringOAuth2RefreshToken;
import com.chinasofti.oauth2.asserver.entity.DefaultOAuth2AccessToken;
import com.chinasofti.oauth2.asserver.entity.DefaultOAuth2RefreshToken;
import com.chinasofti.oauth2.asserver.entity.ExpiringOAuth2RefreshToken;
import com.chinasofti.oauth2.asserver.entity.OAuth2AccessToken;
import com.chinasofti.oauth2.asserver.entity.User;
import com.chinasofti.oauth2.asserver.request.SignSecurityOAuthClient;
import com.chinasofti.oauth2.asserver.request.URLConnectionClient;
import com.chinasofti.oauth2.asserver.response.OAuthASResponse;
import com.chinasofti.oauth2.asserver.securitycheck.Identifier;
import com.chinasofti.oauth2.asserver.securitycheck.SecurityCheck;
import com.chinasofti.oauth2.asserver.service.ClientService;
import com.chinasofti.oauth2.asserver.service.OAuthService;
import com.chinasofti.oauth2.asserver.service.UserService;
import com.chinasofti.oauth2.exception.OAuth2AuthenticationException;
import com.chinasofti.oauth2.exception.OAuth2Exception;
import com.chinasofti.oauth2.key.TokenCoder;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthResourceResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings({"unchecked","rawtypes","unused"})
@RestController
public class AccessTokenController {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenController.class);

    @Value("#{cfg['rs.tokenInfoUrl']}")
    private String tokenInfoUrl;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SecurityCheck clientIdSecurityCheck;

    @Autowired
    private SecurityCheck clientSecretSecurityCheck;

    @Autowired
    private SecurityCheck oauthCodeSecurityCheck;

    @Autowired
    private SecurityCheck refreshTokenSecurityCheck;

    @Autowired
    private SecurityCheck getTokenSecurityCheck;
    
    @Autowired
    private UserService userService;
    
    private String pattern = "YYYY-MM-dd HH:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(pattern);

	@RequestMapping("/checkAccessToken")
    public HttpEntity check(HttpServletRequest request) throws OAuthSystemException {
        String accessToken = request.getParameter(OAuth.OAUTH_ACCESS_TOKEN);
        logger.info("验证token逻辑======> accessToken=" + accessToken);
        boolean falg = oAuthService.checkAccessToken(accessToken);
        logger.info("验证token结果======| flag=" + falg);
        //生成OAuth响应
        OAuthResponse response = OAuthASResponse
                .tokenResponse(HttpServletResponse.SC_OK)
                .setParam(Constant.HTTP_RESPONSE_PARAM_SUCCESS,"" + falg)
                .buildJSONMessage();
        //根据OAuthResponse生成ResponseEntity
        return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
    }

    @RequestMapping("/accessToken")
    public HttpEntity token(HttpServletRequest request) throws OAuthSystemException {
    	long startTime = System.currentTimeMillis();
        try {
            logger.info("获取token========>start");
            //构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            logger.info("====== authCode=" + authCode);

            //目前只支持两种获取token模式(授权码和刷新令牌模式)，其他模式返回不支持
            String grantType = oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE);
            if(!grantType.equals(GrantType.AUTHORIZATION_CODE)&&grantType.equals(GrantType.REFRESH_TOKEN)){
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.UNSUPPORTED_GRANT_TYPE)
                                .setErrorDescription(Constants.UNSUPPORTED_GRANT_TYPE)
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }


            String clientId=oauthRequest.getClientId();
            String userId="";
            //TODO 这里要多加一些安全性认证

            //检查提交的客户端id是否正确
            Identifier identifier = new Identifier(clientIdSecurityCheck);
            if(identifier.verificationTime(request)){//判断是否是过期
                logger.info("clientIdSecurityCheck==========>该访问由于client_id频繁错误已被禁止，未达到解禁时间");
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            if (!oAuthService.checkClientId(oauthRequest.getClientId())) {
                logger.info("checkClientId==========>无效的client_id");
                identifier.isReplyToken(request);
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }else{
                identifier.removeKey(request);
            }

            // 检查客户端安全KEY是否正确
            identifier = new Identifier(clientSecretSecurityCheck);
            if(identifier.verificationTime(request)){//判断是否是过期
                logger.info("clientSecretSecurityCheck==========>该访问由于client_secret频繁错误已被禁止，未达到解禁时间");
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            if (!oAuthService.checkClientSecret(oauthRequest.getClientSecret())) {
                logger.info("checkClientSecret==========>无效的client_secret");
                identifier.isReplyToken(request);
                OAuthResponse response =
                        OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                                .setErrorDescription(Constants.INVALID_CLIENT_DESCRIPTION)
                                .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }else{
                identifier.removeKey(request);
            }

            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                logger.info("AUTHORIZATION_CODE==========>基于授权码获取token");
                identifier = new Identifier(oauthCodeSecurityCheck);
                if(identifier.verificationTime(request)){//判断是否是过期
                    logger.info("oauthCodeSecurityCheck==========>该访问由于code频繁错误已被禁止，未达到解禁时间");
                    OAuthResponse response =
                            OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                    .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                    .setErrorDescription(Constants.AUTHORIZATION_CODE_TOO_MANY_CONNECTION)
                                    .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
                userId=oAuthService.getUserIdByAuthCode(authCode);
                if (!oAuthService.checkAuthCode(authCode)) {
                    logger.info("checkAuthCode==========>无效的code");
                    identifier.isReplyToken(request);
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription(Constants.INVALID_AUTHORIZATION_CODE)
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }else{
                    identifier.removeKey(request);
                }
                
                //检查授权码是否已经换取token
                identifier = new Identifier(getTokenSecurityCheck);
                if(identifier.verificationTime(request)){//判断是否是过期
                    logger.info("getTokenSecurityCheck==========>该访问由于频繁使用已作废的code获取token已被禁止，未达到解禁时间");
                    OAuthResponse response =
                            OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                    .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                    .setErrorDescription(Constants.AUTHORIZATION_CODE_TOO_MANY_CONNECTION)
                                    .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
                if(oAuthService.isGetToken(authCode)){
                    logger.info("isGetToken==========>该code已换取过token，不能再次使用 ");
                    identifier.isReplyToken(request);
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription(Constants.AUTHORIZATION_CODE_IS_USED)
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }else{
                    identifier.removeKey(request);
                }
            }

            // TODO 实现refresh_token方式的认证
            // 检查验证类型，此处只检查AUTHORIZATION_CODE类型，其他的还有PASSWORD或REFRESH_TOKEN
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.REFRESH_TOKEN.toString())) {
                logger.info("REFRESH_TOKEN==========>基于刷新令牌获取token");
                identifier = new Identifier(refreshTokenSecurityCheck);
                if(identifier.verificationTime(request)){//判断是否是过期
                    logger.info("refreshTokenSecurityCheck==========>该访问由于refresh_token频繁错误已被禁止，未达到解禁时间");
                    OAuthResponse response =
                            OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                    .setError(OAuthError.TokenResponse.INVALID_GRANT)
                                    .setErrorDescription(Constants.REFRESH_TOKEN_TOO_MANY_CONNECTION)
                                    .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
                String pRefreshToken = oauthRequest.getParam(OAuth.OAUTH_REFRESH_TOKEN);
                userId=oAuthService.getUserIdByRefreshToken(pRefreshToken);
                if (!oAuthService.checkRefreshToken(pRefreshToken)) {
                    logger.info("checkRefreshToken==========>无效的刷新令牌");
                    identifier.isReplyToken(request);
                    OAuthResponse response = OAuthASResponse
                            .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription(Constants.INVALID_REFRESH_TOKEN)
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }else{
                    identifier.removeKey(request);
                }
            }


            Client client = clientService.findByClientId(clientId);

            /* 更改现有token中信息获取方式
            String userName = oAuthService.getUsernameByAuthCode(authCode);
            User user = new User();
            user.setUsername(userName);
            */
            //改为调用资源服务器配置获取token中信息
            String userInfo = getUserInfo(clientId,userId);

            logger.info("获取令牌混淆信息userInfo==========>"+userInfo);
            long start1Time = System.currentTimeMillis();
            logger.info("======截止获取到令牌混淆信息耗时：" + (start1Time-startTime) + "ms");


            //生成Access Token
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            final String gAccessToken = oauthIssuerImpl.accessToken();
            final String gRefreshToken = oauthIssuerImpl.refreshToken();
//            final String gAccessToken= TokenCoder.token(userInfo.getBytes(),client.getSecretKeyPublic());
//            final String gRefreshToken=TokenCoder.token(new MD5Generator().generateValue().getBytes(), client.getSecretKeyPublic());
            long nowTime=System.currentTimeMillis();

            OAuth2AccessToken accessToken = createAccessToken(gAccessToken, client.getAccessTokenValidity(), nowTime, gRefreshToken);
            //OAuth2RefreshToken refreshToken = createRefreshToken(gRefreshToken, client.getRefreshTokenValidity(), nowTime);


            oAuthService.addToken(gAccessToken, gRefreshToken, userId, clientId, nowTime);
            //oAuthService.addAccessToken(gAccessToken, gRefreshToken, userId, clientId, nowTime);
            //oAuthService.addRefreshToken(gAccessToken, gRefreshToken, userId, clientId, nowTime);

            logger.info("令牌生成成功==========>" + gAccessToken);

            String expirationStr = sdf.format(accessToken.getExpiration());

            //生成OAuth响应
            OAuthResponse response = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(gAccessToken)
                    .setExpiresIn(String.valueOf(accessToken.getExpiresIn()))
                    .setExpiration(expirationStr)
                    .setRefreshToken(gRefreshToken)
                    .buildJSONMessage();

            //根据OAuthResponse生成ResponseEntity
            long endTime = System.currentTimeMillis();
            logger.info("======令牌接口总耗时：" + (endTime-startTime) + "ms");
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e){
            OAuthResponse response = OAuthASResponse
                    .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(e.getError())
                    .setErrorDescription(e.getDescription())
                    .buildJSONMessage();
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
	    } catch (Exception e){
	    	logger.error("获取令牌异常", e);
	    	OAuthResponse response = OAuthASResponse
	    			.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
	    			.setError(OAuthError.OAUTH_ERROR)
	    			.setErrorDescription(Constants.GET_TOKEN_ERROR)
	    			.buildJSONMessage();
	    	return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
	    }
    }


    /**
     * 
     * @param accessToken
     * @param validityDays 过期时间
     * @param beginTime
     * @param refreshToken
     * @return
     */
    private OAuth2AccessToken createAccessToken(String accessToken,long validityDays,long beginTime,String refreshToken) {
        DefaultOAuth2AccessToken asToken = new DefaultOAuth2AccessToken(accessToken);
        if (validityDays > 0) {
            asToken.setExpiration(new Date(beginTime + (validityDays * 1000L * 3600 * 24)));
        }
        asToken.setRefreshToken(refreshToken == null ? null : new DefaultOAuth2RefreshToken(refreshToken));

        return asToken;
    }

    private ExpiringOAuth2RefreshToken createRefreshToken(String refreshToken,long validitySeconds,long beginTime) {

        ExpiringOAuth2RefreshToken asRefreshToken = new DefaultExpiringOAuth2RefreshToken(refreshToken,
                new Date(beginTime + (validitySeconds * 1000L * 3600 * 24)));
        return asRefreshToken;
    }


    private String getUserInfo(String clientId,String userId) throws OAuth2Exception {
    	logger.info("提供给单点获取用户部分信息以生成令牌 clientId=" + clientId + ",userId=" + userId);
		if (clientId == null || userId == null) {
			return null;
		}
		User user = userService.findOne(userId);
		String userInfos = JSON.toJSONString(user);
		logger.info("获取到的用户信息 " + userInfos);
		return userInfos;
    }


    //TODO 实现接口调用
    private String getUserInfoByRsServer(String clientId,String userId)throws OAuth2Exception {

        SignSecurityOAuthClient oAuthClient=new SignSecurityOAuthClient(new URLConnectionClient());
        JsonParser parser=new JsonParser();
        JsonObject person;
        try {
            OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("token_info_url", tokenInfoUrl, com.chinasofti.oauth2.common.OAuth.HttpMethod.POST);

            userInfoRequest.setParameter("clientId",clientId);
            userInfoRequest.setParameter("userId",userId);

            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
            String out = resourceResponse.getBody();
            person = parser.parse(out).getAsJsonObject();
            String result=person.get("result").getAsString();
            if(result.equals("250")){
                return new Timestamp(System.currentTimeMillis()).toString();
            }else{
                return person.get("rows").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuth2AuthenticationException(e);
        }
    }

}
