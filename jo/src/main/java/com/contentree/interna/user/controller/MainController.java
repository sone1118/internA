package com.contentree.interna.user.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.service.MypageService;
import com.contentree.interna.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {
	
	private final UserService userService;
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
    			//최상위 클래스라 null이면 exception을 보내는걸로 바꾸기
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
    
 // [ 손헤진  ] go to withdrawal.html
    @GetMapping("/withdrawal")
    public String joins(@CookieValue(value = "refresh", required = false) String refresh, Model model) {

    	if(refresh == null) {
        	log.info("refresh 쿠키가 없습니다. login으로 이동합니다.");
        	model.addAttribute("error_message", "잘못된 접근입니다");
    		return "login1";
    	}
    	else {
    		return "withdrawal";
    	}    	
    }
    
	// [ 손헤진 ] go to joins.html
    @GetMapping("/joins")
    public String withdrawal(@CookieValue(value = "refresh", required = false) String refresh, Model model) {
    	
    	if(refresh == null) {
        	log.info("refresh 쿠키가 없습니다. login으로 이동합니다.");
        	model.addAttribute("error_message", "잘못된 접근입니다");
    		return "login1";
    	}
    	else {
    		return "joins";
    	}   
    }
    
 // [ 김지슬 ] go to megabox.html
    @GetMapping("/megabox")
    public String megabox(Principal principal, Model model) {
    	String userName = userService.getUserName(Long.parseLong(principal.getName()));
    	
    	model.addAttribute("userName", userName);
    	return "megabox";
    }
}
