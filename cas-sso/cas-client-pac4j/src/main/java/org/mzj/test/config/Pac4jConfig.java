package org.mzj.test.config;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.buji.pac4j.context.ShiroSessionStore;

/**
 * buji-pac4j 是 Shiro Web 应用程序的一个简单而强大的安全库，shiro-cas的替代方案；
 *    支持身份验证和授权，还提供了高级功能，如 CSRF 保护；
 *   基于 Java 8、Shiro 1.4 和 pac4j 安全引擎 v2；
 * @author Administrator
 *
 */
@Configuration
@DependsOn("appConfig")
public class Pac4jConfig {
	private String casServerUrl = AppConfig.casServerUrl;
	private String projectUrl = AppConfig.projectUrl;
	private String clientName = AppConfig.clientName;

	/**
	 * pac4j配置
	 * 
	 * @param casClient
	 * @param shiroSessionStore
	 * @return
	 */
	@Bean("authcConfig")
	public Config config(CasClient casClient, ShiroSessionStore shiroSessionStore) {
		Config config = new Config(casClient);
		config.setSessionStore(shiroSessionStore);
		return config;
	}

	/**
	 * 自定义存储
	 * 
	 * @return
	 */
	@Bean
	public ShiroSessionStore shiroSessionStore() {
		return new ShiroSessionStore();
	}

	/**
	 * cas 客户端配置
	 * 
	 * @param casConfig
	 * @return
	 */
	@Bean
	public CasClient casClient(CasConfiguration casConfig) {
		CasClient casClient = new MyCasClient(casConfig);
		// 客户端回调地址
		casClient.setCallbackUrl(projectUrl + "/callback?client_name=" + clientName);
		casClient.setName(clientName);
		return casClient;
	}

	/**
	 * 请求cas服务端配置
	 * 
	 * @param casLogoutHandler
	 */
	@Bean
	public CasConfiguration casConfig() {
		final CasConfiguration configuration = new CasConfiguration();
		// CAS server登录地址
		configuration.setLoginUrl(casServerUrl + "/login");
		// CAS 版本，默认为 CAS30，我们使用的是 CAS20
		configuration.setProtocol(CasProtocol.CAS20);
		configuration.setAcceptAnyProxy(true);
		configuration.setPrefixUrl(casServerUrl + "/");
		return configuration;
	}

}
