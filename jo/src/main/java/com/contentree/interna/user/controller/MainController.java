package com.contentree.interna.user.controller;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {
	
	@Value("${spring.cookie.refresh-cookie-name}")
    private String refreshCookieName;
	
	private final MypageService mypageService;
	
	private final CookieUtil cookieUtil;
	
	// [ 손혜진 ] go to login.html with error message
    @GetMapping("/")
    public String home(@RequestParam(value = "error", required = false) String error, HttpServletRequest request,
    		Principal principal, Model model) {
    	Cookie refreshCookie = cookieUtil.getCookie(request, refreshCookieName);
    	
    	//1. 쿠키가 없으면 error와 함께 login 으로 이동
    	if(refreshCookie == null) {
        	log.info("refresh 쿠키가 없습니다. login으로 이동합니다.");
        	model.addAttribute("error_message", error);
    		return "login";
    	}

    	//2. 인증된 사용자라면 home으로 가기
		log.info("refresh쿠키가 있습니다. home으로 이동합니다.");
		try {
    		HomeGetUserDetailRes user =  mypageService.getUserDetail(Long.parseLong(principal.getName()));

    		//user가 없을경우 login으로
    		if(user == null) {
    			log.error("user을 찾지 못했습니다.");
    			model.addAttribute("error_message", "잘못된 접근입니다.");
    			return "login";
    		}
    		
    		log.info("사용자 정보가 잘 받아졌습니다");
    		model.addAttribute("user", user);
    		return "home";
    		
		}catch(Exception e) {
			
			log.error("사용자 정보가 없습니다.");
			model.addAttribute("error_message", "사용자 정보가 없습니다.");
			return "login";
		}		
    }
    
 // [ 손헤진 ] go to mypage.html
    @GetMapping("/mypage")
    public String home(HttpServletRequest request, Principal principal, Model model) {
  	
    	//1.쿠키가 없으면 login화면 시큐리티가 알아서 해줄것
    	
    	//2. 쿠키가 있으면 mypage
		log.info("refresh쿠키가 있습니다. mypage으로 이동합니다.");
   
		try {
			MypageGetUserDetailRes user =  mypageService.getUserDetailWithStar(Long.parseLong(principal.getName()));
    		
    		log.info("사용자 정보가 잘 받아졌습니다");
    		model.addAttribute("user", user);
    		return "mypage";
    		
		} catch(NullPointerException e) {
			
			log.error("사용자 정보가 없습니다.");
			model.addAttribute("error", "잘못된 접근입니다.");
			return "redirect:/";
		}		
    	
    }
    
 // [ 손헤진 ] go to withdrawal.html
    @GetMapping("/withdrawal")
    public String joins(HttpServletRequest request, Principal principal, Model model) {
    	//1. 쿠키가 없으면 login 시큐리티가 걸러줄것
    	//2. 쿠키가 있으면 탈퇴 페이지 이동가능
    	return "withdrawal";  	
    }
    
	// [ 손헤진 ] go to withdrawalLast.html
    @GetMapping("/withdrawalLast")
    public String withdrawalLast(HttpServletRequest request, Principal principal, Model model) {
    	//1. 쿠키가 없으면 login 시큐리티가 걸러줄것
    	//2. 휴대폰 인증이완료된 회원임을 인증하는 부분 (개발 예정)
    	
    	//3. 인증이 완료된 회원이면 withdrawalLast로 이동
    	return "withdrawalLast";   
    }
    
	// [ 손헤진 ] go to joins.html
    @GetMapping("/joins")
    public String withdrawal(HttpServletRequest request, Principal principal, Model model) {
    	//1. 쿠키가 없으면 login 시큐리티가 걸러줄것
    	
    	//2. 인증이 완료된 회원이면 joins인증한 user인지 확인
    	try {		
    		HomeGetUserDetailRes user =  mypageService.getUserDetail(Long.parseLong(principal.getName()));
    		
    		//2.1.joins 인증한 회원이라면 인증 페이지 보여줄 필요없음
    		if(user.getUserRole()) {
    			log.error("이미 회원 인증한 user 입니다.");
    			return "redirect:/mypage";
    		} 
    		
    		//2.2 joins 인증을 안한 회원이라면 joins 페이지 보여줌
    		return "joins";
    		
    	}catch(NullPointerException e){
    		
    		log.error("사용자 정보가 업습니다.");
    		model.addAttribute("error", "잘못된 접근입니다.");
			return "redirect:/";
    	}
    }

}
