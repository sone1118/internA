package com.contentree.interna.user.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.contentree.interna.user.dto.HomeGetUserDetailRes;
import com.contentree.interna.user.dto.MypageGetUserDetailRes;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class LoginController {
	
	private final MypageService mypageService;
	
	// [ 손혜진 ] go to login.html with error message
    @GetMapping("/")
    public ModelAndView home(@RequestParam(value = "error", required = false) String error) {

    	ModelAndView model = new ModelAndView();
    	model.setViewName("home");
    	//error가 있으면 error메세지 보내서 출력하기
    	if(error != null) model.addObject("error_message", error);
    	
    	return model;
    }
    
	// [ 손헤진 ] 로그인후 home화면에서 정보를 요청할때 => 이름, 조인스, 등급, 생일
	@PostMapping("/api/users")
	public ResponseEntity<HomeGetUserDetailRes> getHomeDetail(HttpServletRequest request) throws IOException {
		log.info("/api/mypage 요청이 들어옴.");
		
		//Principal principal 
		// principal.getname(); //String -> userSeq임
		//Principal principal에서 userSeq를 꺼내서 사용
		
		//이거는 임시값
		String userSeqString = request.getHeader("Authorization");
		if(userSeqString == null) return ResponseEntity.status(400).body(null);
		Long userSeq = Long.parseLong(userSeqString);
		log.info("요청이 들어온 userSeq " + userSeq);
			
		//유저 정보를 받아온다
		//시큐리이에서 걸러 줄거라 try catch 안해도 되지만 일단 해줌
		try {
			HomeGetUserDetailRes user =  mypageService.getUserDetail(userSeq);
			log.info("유저가 있습니다.");
			return ResponseEntity.status(200).body(user);
		}catch(Exception e) {
			log.error("유저를 못찾았어요");
			return ResponseEntity.status(400).body(null);
		}
	}
}