package com.zx.frontend.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="test/")
public class Test {
	
	@RequestMapping(value = "good")
	public String test(){
		return "/test/test";
	}
}
