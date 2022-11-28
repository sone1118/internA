package com.contentree.interna.user.controller;

import java.util.Objects;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
class LoginController {
	
    @RequestMapping("/")
    public ModelAndView beforeLogin(@RequestParam(value = "login_check", required = false) Boolean login_check) {

    	//로그인된 사용자인지 아닌지 확인한후
    	Boolean login = true;
//    	Boolean login;
//    	if(Objects.isNull(login_check)) {
//    		//로그인 한것인지 아닌지 체크후
//    		login = true;
//    	}else {
//    		login = false;
//    	}

    	ModelAndView model = new ModelAndView();
    	model.setViewName("login");
      	model.addObject("login", login);
        
      	//로그인된 사용자라면 정보를 받아서 넘기고
    	if(login) {
    		model.addObject("name", "김지슬");
    		model.addObject("birth", true);
    		model.addObject("level", "G");
    		model.addObject("joins", true);
    	}else { 
    		//로그인 되지 않은 사용자라면 error 확인
    		Boolean error = true;
    		String error_message = (error)? "중복 가입된 정보가 있습니다!" : "";
          	model.addObject("error_message", error_message);
    	}
    	
    	return model;
    }
    
    
    
//    @RequestMapping("/test")
//    public ModelAndView testLogin() {
//    	ModelAndView model = new ModelAndView();
//    	model.setViewName("login");	
//    	return model;
//    }
//    
    //로그인 할때 local 확인해서 token 있으면 로그인 후 화면 보여주면됨.

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
