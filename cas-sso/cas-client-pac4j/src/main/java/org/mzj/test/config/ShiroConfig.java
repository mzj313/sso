package org.mzj.test.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.engine.SecurityLogic;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.web.filter.DelegatingFilterProxy;

import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;

/**
 * 
 * @author Administrator
 *
 */
@Configuration
@DependsOn("appConfig")
public class ShiroConfig {
	private String projectUrl = AppConfig.projectUrl;
	private String casServerUrl = AppConfig.casServerUrl;
	private String clientName = AppConfig.clientName;
	
	@Bean("securityManager")
	public DefaultWebSecurityManager securityManager(Pac4jSubjectFactory subjectFactory, SessionManager sessionManager,
			CasRealm casRealm) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(casRealm);
		manager.setSubjectFactory(subjectFactory);
		manager.setSessionManager(sessionManager);
		return manager;
	}

	@Bean
	public CasRealm casRealm() {
		// 使用自定义的realm
		CasRealm realm = new CasRealm();
		realm.setName(clientName);
		// 暂时不使用缓存
		realm.setCachingEnabled(false);
		realm.setAuthenticationCachingEnabled(false);
		realm.setAuthorizationCachingEnabled(false);
		// realm.setAuthenticationCacheName("authenticationCache");
		// realm.setAuthorizationCacheName("authorizationCache");
		return realm;
	}

	/**
	 * 使用 pac4j 的 subjectFactory
	 * @return
	 */
	@Bean
	public Pac4jSubjectFactory subjectFactory() {
		return new Pac4jSubjectFactory();
	}

	@Bean
	public FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean() {
		FilterRegistrationBean<DelegatingFilterProxy> filterRegistration = new FilterRegistrationBean<DelegatingFilterProxy>();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		// 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
		filterRegistration.addInitParameter("targetFilterLifecycle", "true");
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		filterRegistration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
		return filterRegistration;
	}

	/**
	 * shiroFilter
	 * 
	 * @param securityManager
	 * @param config
	 * @param securityLogic 
	 * @return
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager, Config config, SecurityLogic<Object, J2EContext> securityLogic) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		// 添加casFilter到shiroFilter中
		this.loadShiroFilterChain(shiroFilterFactoryBean);
		Map<String, Filter> filters = new HashMap<>(3);
		
		// cas资源认证拦截器  判断当前用户是否已经认证通过并且校验是否有权限访问此url
		SecurityFilter securityFilter = new SecurityFilter();
		securityFilter.setConfig(config);
		securityFilter.setClients(clientName);
		securityFilter.setSecurityLogic(securityLogic);
		filters.put("securityFilter", securityFilter);
		
		// cas认证后回调拦截器  例如CAS的ST校验就是这里完成
		CallbackFilter callbackFilter = new CallbackFilter();
		callbackFilter.setConfig(config);
		callbackFilter.setDefaultUrl(projectUrl);
		filters.put("callbackFilter", callbackFilter);
		
		// 注销 拦截器
		LogoutFilter logoutFilter = new LogoutFilter();
		logoutFilter.setConfig(config);
		logoutFilter.setCentralLogout(true);
		logoutFilter.setLocalLogout(true);
		logoutFilter.setDefaultUrl(projectUrl + "/callback?client_name=" + clientName);
		filters.put("logout", logoutFilter);
		
		shiroFilterFactoryBean.setFilters(filters);
		return shiroFilterFactoryBean;
	}
	
	@Bean
	@SuppressWarnings("rawtypes")
	protected MyShiroSecurityLogic securityLogic() {
		MyShiroSecurityLogic securityLogic = new MyShiroSecurityLogic();
		return securityLogic;
	}

	/**
	 * 加载shiroFilter权限控制规则（从数据库读取然后配置）
	 * 
	 * @param shiroFilterFactoryBean
	 */
	private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		filterChainDefinitionMap.put("/a/**", "securityFilter");
		filterChainDefinitionMap.put("/callback", "callbackFilter");
		// io.buji.pac4j.filter.LogoutFilter
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/**", "anon");
		// filterChainDefinitionMap.put("/user/edit/**", "authc,perms[user:edit]");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
	}

	@Bean
	public SessionDAO sessionDAO() {
		MemorySessionDAO memorySessionDAO = new MemorySessionDAO() {
			public Session readSession(Serializable sessionId) throws UnknownSessionException {
		        Session s = doReadSession(sessionId);
		        if (s == null) {
		        	System.err.println("There is no session with id [" + sessionId + "]");
		        }
		        return s;
		    }
		};
		return memorySessionDAO;
	}

	/**
	 * 自定义cookie名称
	 * @return
	 */
	@Bean
	public SimpleCookie sessionIdCookie() {
		SimpleCookie cookie = new SimpleCookie("sid");
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		cookie.setHttpOnly(false);
		return cookie;
	}

	@Bean
	public DefaultWebSessionManager sessionManager(SimpleCookie sessionIdCookie, SessionDAO sessionDAO) {
		DefaultWebSessionManager sessionManager = new MySessionManager();
		// 不用默认的JSESSIONID，当跳出SHIRO SERVLET时jetty容器会为JSESSIONID重新分配值导致登录会话丢失；
		// 未解决！DefaultSessionManager.retrieveSession里报UnknownSessionException
		// sessionIdCookie.setName("mysessionid");
		sessionManager.setSessionIdCookie(sessionIdCookie);
		sessionManager.setSessionIdCookieEnabled(true);
		// 会话超时时间，单位：毫秒
		sessionManager.setGlobalSessionTimeout(3600*1000);
		sessionManager.setSessionDAO(sessionDAO);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		return sessionManager;
	}

	/**
	 * 下面的代码是添加注解支持
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		// 强制使用cglib，防止重复代理和可能引起代理出错的问题
		// https://zhuanlan.zhihu.com/p/29161098
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	@Bean
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	@Bean
	public FilterRegistrationBean<SingleSignOutFilter> singleSignOutFilter() {
		FilterRegistrationBean<SingleSignOutFilter> bean = new FilterRegistrationBean<SingleSignOutFilter>();
		bean.setName("singleSignOutFilter");
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setCasServerUrlPrefix(casServerUrl);
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		bean.setFilter(singleSignOutFilter);
		bean.addUrlPatterns("/*");
		bean.setEnabled(true);
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}