package com.contentree.interna.user.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.dto.HomeGetUserDetailRes;
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
	
	// [ 손헤진 ] go to mypage.html
	@GetMapping("/mypage")
    public ModelAndView mypage() {    
		ModelAndView model = new ModelAndView();
		model.setViewName("mypage");
    	return model;
    }
	
	// [ 손헤진 ] go to joins.html
	@GetMapping("/joins")
    public ModelAndView joinsCertification() {    
		ModelAndView model = new ModelAndView();
		model.setViewName("joins");
    	return model;
    }

	
	// [ 손헤진 ] mypage에서 정보를 요청할때 => 이름, 이메일, 생년월일, 가입일, 회원등급
	@PostMapping("/api/mypage")
	public ResponseEntity<MypageGetUserDetailRes> getMypageDetail(HttpServletRequest request) throws IOException {
		log.info("/api/mypage 요청이 들어옴.");
		//Principal principal 
		// principal.getname(); //String -> userSeq임
		//Principal principal에서 userSeq를 꺼내서 사용
		//이거는 임시값
		String userSeqString = request.getHeader("Authorization");
		Long userSeq = Long.parseLong(userSeqString);
		log.info("요청이 들어온 userSeq" + userSeq);
		

		//유저 정보를 받아온다
		//시큐리이에서 걸러 줄거라 try catch 안해도 되지만 일단 해줌
		try {
			MypageGetUserDetailRes user = mypageService.getUserDetailWithStar(userSeq);
			log.info("유저가 있습니다.");
			return ResponseEntity.status(200).body(user);
		}catch(Exception e) {
			log.error("유저를 못찾았어요");
			return ResponseEntity.status(400).body(null);
		}
	}
}