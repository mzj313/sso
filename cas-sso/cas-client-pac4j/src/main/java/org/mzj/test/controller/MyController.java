package org.mzj.test.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mzj.test.util.ShiroUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

	@RequestMapping("")
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		String currentUserName = ShiroUtils.getCurrentUserName();
		logger.info("index: username=" + currentUserName);
		model.addAttribute("username", currentUserName);
		return "index";
	}
	
	@RequestMapping("/a")
	@ResponseBody
	public String a() {
		LocalDateTime now = LocalDateTime.now();
		String currentUserName = ShiroUtils.getCurrentUserName();
		logger.info("aaa: username=" + currentUserName);
		return "aaa. " + currentUserName + " " + now.format(formatter);
	}

	@RequestMapping("/b")
	@ResponseBody
	public String b() {
		LocalDateTime now = LocalDateTime.now();
		String currentUserName = ShiroUtils.getCurrentUserName();
		logger.info("bbb: username=" + currentUserName);
		String result = "bbb. " + currentUserName + " " + now.format(formatter);
		return result;
	}
	
	@RequestMapping("/c")
	public String c(HttpServletResponse response) {
		LocalDateTime now = LocalDateTime.now();
		String currentUserName = ShiroUtils.getCurrentUserName();
		if (StringUtils.isEmpty(currentUserName)) {
			//return "redirect:http://localhost:8280/cas/login?service=http%3A%2F%2Flocalhost%3A8282%2Fcallback%3Fclient_name%3DCasClient";
			return "login";
		}
		logger.info("ccc: username=" + currentUserName);
		String result = "ccc. " + currentUserName + " " + now.format(formatter);
		// 让浏览器用UTF-8来解析返回的数据
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		// 告诉servlet用UTF-8转码，而不是用默认的ISO8859
		response.setCharacterEncoding("UTF-8");
		// 不需要return返回值
		try {
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
