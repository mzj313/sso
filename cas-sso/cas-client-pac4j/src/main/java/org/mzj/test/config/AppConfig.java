package org.mzj.test.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
	/** cas地址 */
	public static String casServerUrl;

	/** 验证返回后的应用地址 */
	public static String projectUrl;

	/** 相当于一个标志，可以随意 */
	public static String clientName;
	
//	@Value("${cas.server.url}")
//	public String casServerUrlTmp;
//	@Value("${cas.project.url}")
//	public String projectUrlTmp;
//	@Value("${cas.client-name}")
//	public String clientNameTmp;
	
	AppConfig(){
		System.out.println("====AppConfig...");
	}
	
//	@PostConstruct 
//	public void init() {
//		AppConfig.casServerUrl = casServerUrlTmp;
//		AppConfig.projectUrl = projectUrlTmp;
//		AppConfig.clientName = clientNameTmp;
//	}

	@Value("${cas.server.url}")
	public void setCasServerUrl(String casServerUrl) {
		AppConfig.casServerUrl = casServerUrl;
	}

	@Value("${cas.project.url}")
	public void setProjectUrl(String projectUrl) {
		AppConfig.projectUrl = projectUrl;
	}

	@Value("${cas.client-name}")
	public void setClientName(String clientName) {
		AppConfig.clientName = clientName;
	}

}
