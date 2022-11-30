package com.contentree.interna.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
class LoginController {
	
    @RequestMapping("/")
    public ModelAndView home(@RequestParam(value = "error", required = false) String error) {

    	ModelAndView model = new ModelAndView();
    	model.setViewName("home");
    	//error가 있으면 error메세지 보내서 출력하기
    	if(error != null) model.addObject("error_message", "중복 가입된 정보가 있습니다.");
    	
    	return model;
    }

}