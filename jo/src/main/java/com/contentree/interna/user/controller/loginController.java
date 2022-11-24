package com.contentree.interna.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
class loginController {
	
    @RequestMapping("/")
    public ModelAndView beforeLogin() {
    	//boolean login = false;
    	boolean login = true;
    	ModelAndView model = new ModelAndView();
    	model.setViewName("login");
    	model.addObject("login", login);
    	
    	return model;
    }
    
    
//카카오에서 로그인을 한후에 session에 token이 있을때 "/"로 왔을때
//    
//    @RequestMapping()
//    public String singnIn(HttpServletRequest request) {
//    	HttpSession session = request.getSession();
//
//    	return "redirect:/";
//    }
    
    //login상태 유지
//    @RequestMapping()
//    public String logIn(Model model) {
//    	model.addAllAttributes("logIn", true);
//    	return login;
//    };
    
}
