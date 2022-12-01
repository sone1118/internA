package com.contentree.interna.user.controller;

import java.io.IOException;
import java.util.Calendar;
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

import com.contentree.interna.global.common.response.TestResponse2;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
class LoginController {
	
	private final MypageService mypageService;
	
	//단순히 login으로 보내줌
	//에러 메세지를 화면에 보여준다.
    @GetMapping("/")
    public ModelAndView home(@RequestParam(value = "error", required = false) String error) {

    	ModelAndView model = new ModelAndView();
    	model.setViewName("home");
    	//error가 있으면 error메세지 보내서 출력하기
    	if(error != null) model.addObject("error_message", error);
    	
    	return model;
    }
    
	//로그인후 home화면에서 정보를 요청할때
	//이름, 조인스, 등급, 생일
	@PostMapping("/api/users")
	public ResponseEntity<TestResponse2> getUserD(HttpServletRequest request) throws IOException {
		//서비스 로직 넣기
		//Principal principal 
		// principal.getname(); //String -> userSeq임
		System.out.println(request.getHeader("Authorization"));
		String userSeqString = request.getHeader("Authorization");
		Long userSeq = Long.parseLong(userSeqString);
		Optional<User> user = mypageService.getUserDetail(userSeq);		
		
		if (user.isPresent()) {
			User user2 = user.get();
			log.error("유저가 있어");
			
			Calendar birth = user2.getUserBirth();
			
			String month = Integer.toString(birth.get(Calendar.MONTH) + 1);
			String day = Integer.toString(birth.get(Calendar.DAY_OF_MONTH));
		
			TestResponse2 testResponse2 = new TestResponse2();
			testResponse2.setUserName(user2.getUserName());
			testResponse2.setUserGrade(user2.getUserGrade());
			testResponse2.setUserRole(user2.getUserRole());
			testResponse2.setUserBirth(month + "-" + day);
			
			return ResponseEntity.status(200).body(testResponse2);
		} else {	
			log.error("유저가 없어");
			return ResponseEntity.status(400).build();
		}		
	}
}