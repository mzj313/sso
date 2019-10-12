package org.mzj.test.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class CASAutoConfig {
	// 这几个名字不能改， 参考cas-client-autoconfig-support包里面的CasClientConfigurationProperties
	@Value("${cas.server-url-prefix}")
	private String serverUrlPrefix;
	@Value("${cas.server-login-url}")
	private String serverLoginUrl;
	@Value("${cas.client-host-url}")
	private String clientHostUrl;

	/**
	 * 登出过滤器
	 * 
	 * @return
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public FilterRegistrationBean<Filter> filterSignoutRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setCasServerUrlPrefix(serverUrlPrefix);
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		registration.setFilter(singleSignOutFilter);
		registration.addUrlPatterns("/*");
		return registration;
	}
	
	/**
	 * 授权过滤器
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<Filter> filterAuthenticationRegistration() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
		registration.setFilter(new AuthenticationFilter());
		// 设定匹配的路径
		registration.addUrlPatterns("/*");
		// 必要参数
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("casServerLoginUrl", serverUrlPrefix);
		initParameters.put("serverName", clientHostUrl);
		initParameters.put("ignorePattern", "/logout|/index");
		registration.setInitParameters(initParameters);
		// 设定加载的顺序
		registration.setOrder(2);
		return registration;
	}
}