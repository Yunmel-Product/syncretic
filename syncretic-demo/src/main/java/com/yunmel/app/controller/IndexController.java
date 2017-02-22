package com.yunmel.app.controller;

import com.yunmel.web.annotation.Controller;
import com.yunmel.web.annotation.Get;
import com.yunmel.web.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping
	public String index() {
		return "index.jsp";
	}
	
	@Get("login")
	public String login() {
		return "index.jsp";
	}
}
