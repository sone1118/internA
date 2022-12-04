package com.contentree.interna.user.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//생서자 주입 어노테이션
@RequiredArgsConstructor
@Controller
class MypageControllerController {
	
	private final MypageService mypageService;	
	
	// [ 손헤진 ] go to mypage.html
    @GetMapping("/mypage")
    public String home(@CookieValue(value = "refresh", required = false) String refresh, Model model) {
    	
    	if(refresh == null) {
        	log.info("refresh 쿠키가 없습니다. login으로 이동합니다.");
    		return "login";
    	}
    	else {
    		log.info("refresh 쿠키가 있습니다. mypage으로 이동합니다.");
    		//임시로 쿠키값으로 seq를 확인하지만 나중에는 principal로 들어오는 값을 보고 (인증이 완료된 사람) seq를 알아낼것
    		Long userSeq = Long.parseLong(refresh);
    		
    		//유저 데이터 얻어오기
    		try {
    			MypageGetUserDetailRes user =  mypageService.getUserDetailWithStar(userSeq);
        		
        		//user가 없을경우 던져버림
        		if(user == null) throw new Exception();
        		log.info("사용자 정보가 잘 받아졌습니다");
        		
        		model.addAttribute("user", user);
        		return "mypage";
        		
    		}catch(Exception e) {
    			System.out.println(e);
    			log.error("사용자 정보가 없습니다.");
    			return "login";
    		}		
    	}
    }
}