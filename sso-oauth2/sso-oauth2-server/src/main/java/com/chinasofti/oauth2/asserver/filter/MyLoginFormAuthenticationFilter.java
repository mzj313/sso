package com.chinasofti.oauth2.asserver.filter;

import com.chinasofti.oauth2.asserver.request.SignSecurityOAuthClient;
import com.chinasofti.oauth2.asserver.request.URLConnectionClient;
import com.chinasofti.oauth2.asserver.shiro.UsernamePasswordCaptchaToken;
import com.chinasofti.oauth2.common.exception.OAuthProblemException;
import com.chinasofti.oauth2.common.exception.OAuthSystemException;
import com.chinasofti.oauth2.common.utils.JSONUtils;
import com.chinasofti.oauth2.request.OAuthResourceClientRequest;
import com.chinasofti.oauth2.response.OAuthResourceResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangkai on 15/5/29.
 */
public class MyLoginFormAuthenticationFilter extends FormAuthenticationFilter {

    //新增验证码功能代码－start

    @Value("#{cfg['rs.logUrl']}")
    private String logUrl;

    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    public String getCaptchaParam() {

        return captchaParam;

    }

    protected String getCaptcha(ServletRequest request) {

        return WebUtils.getCleanParam(request, getCaptchaParam());

    }

    protected AuthenticationToken createToken(

            ServletRequest request, ServletResponse response) {

        String username = getUsername(request);

        String password = getPassword(request);

        String captcha = getCaptcha(request);

        boolean rememberMe = isRememberMe(request);

        String host = getHost(request);

        return new UsernamePasswordCaptchaToken(username,
                password.toCharArray(), rememberMe, host, captcha);

    }
    //新增验证码功能代码－end

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        this.issueSuccessRedirect(request, response);
        return log2Local(token,true);
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        this.setFailureAttribute(request, e);
        return log2Local(token,false);
    }
    
    private boolean log2Local(AuthenticationToken authToken, boolean bLoginSuccess){
    	UsernamePasswordToken token = (UsernamePasswordToken) authToken;
    	System.out.println("======log2Local " + token.getHost() + " " + token.getUsername() + " " + bLoginSuccess);
    	return bLoginSuccess;
    }

    /**
     * 用户登录时，发送远程请求记录日志
     * @param token
     */
    private boolean log2Remote(AuthenticationToken token,boolean flag){
        SignSecurityOAuthClient oAuthClient=new SignSecurityOAuthClient(new URLConnectionClient());
        OAuthResourceClientRequest userInfoRequest = new OAuthResourceClientRequest("logUrl", logUrl, com.chinasofti.oauth2.common.OAuth.HttpMethod.POST);
        Map<String, Object> valueMap = new HashMap<String,Object>();
        valueMap.put("userName", ((UsernamePasswordToken) token).getUsername());
        valueMap.put("ipAddress", ((UsernamePasswordToken) token).getHost());
        String logBeanJson = "";
        //todo 这里如果需要记录其他信息的话，要到数据库查询，目前只能给出用户名称和IP地址

        try {
            logBeanJson = JSONUtils.buildJSON(valueMap);
            userInfoRequest.setParameter("logBeanJson",logBeanJson);
            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuthResourceResponse.class);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return flag;
        }
    }
}
