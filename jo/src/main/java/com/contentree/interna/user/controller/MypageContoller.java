package com.contentree.interna.user.controller;

import java.util.Calendar;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.contentree.interna.user.entity.User;



@RestController
class MypageControllerController {
	
	//private final MypageService mypageService;
	
    @RequestMapping("/mypage")
    public ModelAndView goMypage() {
    	//유저 정보를 얻어온다
    	ModelAndView model = new ModelAndView();
    	model.setViewName("mypage");
  	
    	return model;
    }
    
//    @RequestMapping("/api/users")
//    public User getUserDetails () {
//    	//유저 정보를 얻어온다
//    	
//    	//잘 받아왔다
//    	//받은 유저 정보를 보내준다.
//    	User user;
//  	
//    	return user;
//    }
}