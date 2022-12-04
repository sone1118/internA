package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class LoginController {
	
	private final MypageService mypageService;
	
	// [ 손혜진 ] go to login.html with error message
    @GetMapping("/")
    public String home(@RequestParam(value = "error", required = false) String error, @CookieValue(value = "refresh", required = false) String refresh, Model model) {
    	
    	if(refresh == null) {
        	log.info("refresh 쿠키가 없습니다. login으로 이동합니다.");
        	model.addAttribute("error_message", error);
    		return "login";
    	}
    	else {
    		log.info("refresh 쿠키가 있습니다. home으로 이동합니다.");
    		//임시로 쿠키값으로 seq를 확인하지만 나중에는 principal로 들어오는 값을 보고 (인증이 완료된 사람) seq를 알아낼것
    		Long userSeq = Long.parseLong(refresh);
    		
    		//유저 데이터 얻어오기
    		try {
        		HomeGetUserDetailRes user =  mypageService.getUserDetail(userSeq);
        		
        		//user가 없을경우 던져버림
        		if(user == null) throw new Exception();
        		log.info("사용자 정보가 잘 받아졌습니다");
        		
        		model.addAttribute("user", user);
        		return "home";
        		
    		}catch(Exception e) {
    			System.out.println(e);
    			log.error("사용자 정보가 없습니다.");
    			model.addAttribute("error_message", "사용자 정보가 없습니다.");
    			return "login";
    		}		
    	}
    }
}