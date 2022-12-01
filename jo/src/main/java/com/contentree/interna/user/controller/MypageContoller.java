package com.contentree.interna.user.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.contentree.interna.global.common.response.TestResponse;
import com.contentree.interna.global.common.response.TestResponse2;
import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//생서자 주입 어노테이션
@RequiredArgsConstructor
@Controller
class MypageControllerController {
	
	private final MypageService mypageService;	

	//단순히 mypage.html로 보내준다
	@GetMapping("/mypage")
    public ModelAndView mypage() {    
		ModelAndView model = new ModelAndView();
		model.setViewName("mypage");
    	return model;
    }

	//mypage에서 정보를 요청할때
	//이름, 이메일, 생년월일, 가입일, 회원등급
	@PostMapping("/api/mypage")
	public ResponseEntity<TestResponse> readBody(HttpServletRequest request) throws IOException {
		
		System.out.println(request.getHeader("Authorization"));
		String userSeqString = request.getHeader("Authorization");
		Long userSeq = Long.parseLong(userSeqString);
		Optional<User> user = mypageService.getUserDetail(userSeq);
		
		if (user.isPresent()) {
			User user2 = user.get();
			log.error("유저가 있어");
					
			TestResponse testResponse = new TestResponse();
			testResponse.setUserName(user2.getUserName());
			testResponse.setUserEmail(user2.getUserEmail().replaceAll("[A-Za-z0-9]", "*"));
			testResponse.setUserBirth(user2.getUserBirth());
			testResponse.setUserCreateAt(user2.getUserBirth());
			testResponse.setUserGrade(user2.getUserGrade());
			
			return ResponseEntity.status(200).body(testResponse);
			//return ResponseEntity.status(200).body(user2);
		} else {	
			log.error("유저가 없어");
			return ResponseEntity.status(400).build();
		}
	}
}


