package org.mzj.test.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mzj.test.util.ShiroUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		return "bbb. " + currentUserName + " " + now.format(formatter);
	}
}
