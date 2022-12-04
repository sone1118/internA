package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {
	
	// [ 손헤진 ] go to withdrawal.html
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
}
//쿠키가 없을때는 /로 리다이랙트가 맞는 건지 아니면 걍 thymleaf로 뿌리는게 맞는건지 아직 잘 모르겠어 리다이랙트? 굳이?