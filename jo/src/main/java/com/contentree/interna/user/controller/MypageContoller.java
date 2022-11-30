package com.contentree.interna.user.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
//    @PostMapping("/mypage")
//    public ModelAndView goMypage(HttpServletRequest request) {
//    	//유저 정보를 얻어온다
//    	String userEmail = request.getHeader("Authorization");
//    	//유저 정보를 받아서 view로보내준다.
//    	
//    	ModelAndView model = new ModelAndView();
//
//    	model.setViewName("mypage");
//    	model.addObject("userName", "김지슬");
//    	model.addObject("userEmail", userEmail);
//    	model.addObject("userBirth", "98.01.22");
//    	model.addObject("userCreateAt", userEmail);
//    	model.addObject("userGrade", "G");
//    	
//    	//error가 들어 왔으면 /로 error 메세지 같이 보내준ㄷ
//  	
//    	return model;
//    }
//	@PostMapping("/mypage")
//	public void test(HttpServletRequest req)throws IOException{
//		System.out.println(readBody(req));
//	}

	@PostMapping("/mypage")
	public ModelAndView readBody(HttpServletRequest request) throws IOException {
		
		ModelAndView model = new ModelAndView();
    	model.setViewName("mypage");
    	model.addObject("userName", "김지슬");
    	model.addObject("userBirth", "98.01.22");
    	model.addObject("userGrade", "G");
		//서비스 로직 넣기
		//Principal principal 
		// principal.getname(); //String -> userSeq임
		Long userSeq = 1L;
		Optional<User> user = mypageService.getUserDetail(userSeq);
		if (user.isPresent()) {
			User user2 = user.get();
			log.error(user2.getUserEmail());
			
	    	model.addObject("userEmail", user2.getUserEmail());
	    	model.addObject("userCreateAt", user2.getUserEmail());
		}
		
		return model;
//			String test = request.getHeader("Authorization");
//			System.out.println(test);
//			System.out.println(user.get().getUserEmail());
			
	}
}


