package org.mzj.test.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

/**
 * web.xml里面不能读取properties，所以采用这个办法
 * @author Administrator
 *
 */
public class MyContextLoaderListener extends ContextLoaderListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String propFileName = "application.properties";
		System.out.println("加载" + propFileName + "到Context...");
		Properties properties = new Properties();
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName);
			properties.load(is);
			is.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("casServerUrlPrefix", properties.getProperty("server.url"));
		map.put("casServerLoginUrl", properties.getProperty("server.url") + "/login");
		map.put("serverName", properties.getProperty("client.url"));
		for (Entry<?, ?> entry : map.entrySet()) {
			String key = (String) entry.getKey();
			String value = String.valueOf(entry.getValue());
			System.out.println("加载 " + key + " -> " + value);
			event.getServletContext().setInitParameter(key, value);
		}
		
		super.contextInitialized(event);
	}
}
