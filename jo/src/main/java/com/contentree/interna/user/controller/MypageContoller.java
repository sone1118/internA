package com.contentree.interna.user.controller;

import java.util.Calendar;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;


@RestController
class MypageControllerController {
	
    @RequestMapping("/mypage")
    public ModelAndView goMypage(@RequestParam(value = "login_check", required = false) Boolean login_check) {

    	//유저 정보를 얻어온다
    	Calendar user_date = Calendar.getInstance();
    	user_date.set(2022, 1, 22);
    	
    	//받아온 유저 정보라고 생각하고 넣은 임시값
    	User user = User.builder()
    			.userSeq(1L)
    			.userName("김지슬")
    			.userEmail("sone111@naver.com")
    			.userPhone("01012341234")
    			.userBirth(user_date)
    			.userKakaoId(12L)
    			.userRole(Role.JOINS)
    			.userGrade(Grade.BRONZE)
    			.userAgreeMarketing(true)
    			.userAgreeSns(true)
    			.build();
    	
    	//email은 @기준으로 쪼갠뒤 앞에 아이디는 *처리함
    	//replaceAll(String regex, String replacement) 로 정규식으로 넣자
    	//이름, 이메일, 생년월일, 가입일, 회원등급 가짜 데이터
    	ModelAndView model = new ModelAndView();
    	model.setViewName("mypage");
    	model.addObject("userName", user.getUserName());
    	model.addObject("userEmail", user.getUserEmail());
//    	model.addObject("userYear", user.getUserBirth().get(Calendar.YEAR));
//    	model.addObject("userMonth", user.getUserBirth().get(Calendar.MONTH));
//    	model.addObject("userDate", user.getUserBirth().get(Calendar.DATE));
    	model.addObject("userYear", "****");
    	model.addObject("userMonth", "**");
    	model.addObject("userDate", "**");
    	model.addObject("userCreateAtYear","2022");
    	model.addObject("userCreateAtMonth", "01");
    	model.addObject("userCreateAtDate", "22");
    	model.addObject("userGrade", user.getUserGrade());
        
      	//model.addObject("user", user);
        	
    	return model;
    }
}