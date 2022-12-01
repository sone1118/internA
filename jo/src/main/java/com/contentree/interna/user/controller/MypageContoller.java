package com.contentree.interna.user.controller;

import java.security.Principal;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contentree.interna.global.common.request.MypageSendEmailToJoinsReq;
import com.contentree.interna.global.model.BaseResponseBody;
import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name="마이페이지", description = "마이페이지 API")
@RequestMapping("/api/users")
public class MypageContoller {
	
	private final MypageService mypageService;
	
	private final JwtTokenUtil jwtTokenUtil;
	private final CookieUtil cookieUtil;
	
	
	@Tag(name="마이페이지")
	@PostMapping("/send-joins")
	@Operation(summary = "임직원 인증 이메일 전송", description = "중앙 임직원 여부를 확인하기 위해 joins 이메일에 인증 코드를 전송합니다.")
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
	        @ApiResponse(responseCode = "400", description = "인증 코드 전송 실패")
	        })
    public ResponseEntity<String> sendEmailToJoins(@RequestBody MypageSendEmailToJoinsReq mypageSendEmailToJoinsReq, Principal principal) throws MessagingException {
		log.info("MypageContoller > mypageSendEmailToJoins - 호출 (userSeq : {})", principal.getName());
		Long userSeq = Long.parseLong(principal.getName());
		String joinsId = mypageSendEmailToJoinsReq.getJoinsId();
		
		String answerMessage = mypageService.sendEmailToJoins(userSeq, joinsId);
		if (answerMessage == "2") {
			log.error("MypageContoller > mypageSendEmailToJoins - 이미 인증에 사용된 아이디입니다. (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return ResponseEntity.status(400).body("이미 인증에 사용된 아이디입니다.");
		} else if (answerMessage == "3") {
			log.error("MypageContoller > mypageSendEmailToJoins - 이메일 전송 실패 (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return ResponseEntity.status(400).body("잘못된 아이디 입니다.");
		} else if (answerMessage == "성공") {
			log.info("MypageContoller > mypageSendEmailToJoins - 성공 (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return ResponseEntity.status(200).body("이메일 전송 성공");
		} else {
			log.info("MypageContoller > mypageSendEmailToJoins - 이미 인증된 사용자입니다. (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return ResponseEntity.status(400).body(answerMessage);
		}
		
 
    }
	
	@Tag(name="마이페이지")
	@GetMapping("/login")
	@Operation(summary = "로그인테스트", description = "중앙 임직원 여부를 확인하기 위해 joins 이메일에 인증 코드를 전송합니다.")
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
	        @ApiResponse(responseCode = "400", description = "이메일 전송 실패")
	        })
    public String login(HttpServletResponse response) {
		log.info("sendEmailForJoins - 호출");
		String token = jwtTokenUtil.createAccessToken(1);
		String retString = jwtTokenUtil.createRefreshToken();
		response.addCookie(cookieUtil.createCookie("re-auth", retString));
		return token;
 
    }

}
