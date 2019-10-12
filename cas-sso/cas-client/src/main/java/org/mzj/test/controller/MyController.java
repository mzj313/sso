package org.mzj.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/")
	@ResponseBody
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		// org.jasig.cas.client.util.HttpServletRequestWrapperFilter$CasHttpServletRequestWrapper
		String msg = "index..." + request.getRemoteUser();
		logger.info(msg);
		return msg;
	}
	
	//用于cas登出后回调 http://localhost:8280/cas/logout?service=http://localhost:8281/logout
	@RequestMapping("/logout")
	@ResponseBody
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) {
		String msg = "logout..." + request.getRemoteUser();
		logger.info(msg);
		return msg;
	}
}
