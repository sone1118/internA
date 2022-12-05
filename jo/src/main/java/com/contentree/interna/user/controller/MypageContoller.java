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
import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.MailUtil;
import com.contentree.interna.user.dto.MypageCheckJoinsEmailCodeReq;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.MypageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

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
    public BaseResponseBody sendEmailToJoins(@RequestBody MypageSendEmailToJoinsReq mypageSendEmailToJoinsReq, Principal principal) throws MessagingException {
		log.info("MypageContoller > mypageSendEmailToJoins - 호출 (userSeq : {})", principal.getName());
		Long userSeq = Long.parseLong(principal.getName());
		String joinsId = mypageSendEmailToJoinsReq.getJoinsId();
		
		int answerCode = mypageService.sendEmailToJoins(userSeq, joinsId);
		if (answerCode == 1) {
			return BaseResponseBody.of(400, "이미 인증된 사용자입니다,");
		} else if (answerCode == 2) {
			return BaseResponseBody.of(400, "이미 인증에 사용된 아이디입니다.");
		} else if (answerCode == 3) {
			return BaseResponseBody.of(400, "이메일 전송에 실패하였습니다.");
		} else {
			log.info("MypageContoller > mypageSendEmailToJoins - 성공 (joinsId : {}, userSeq : {})", joinsId, userSeq);
			return BaseResponseBody.of(200, "이메일 전송 성공");
		}
    }
	
	// [ 김지슬 ] 임직원 인증 코드 검증 
	@Tag(name="마이페이지")
	@PostMapping("/check-joins")
	@Operation(summary = "임직원 인증 코드 검증", description = "중앙 임직원 여부를 확인하기 위해 joins 이메일로 전송한 인증코드를 검사합니다.")
	@ApiResponses({
	        @ApiResponse(responseCode = "201", description = "임직원 인증 성공"),
	        @ApiResponse(responseCode = "400", description = "인증 시간 만료"),
	        @ApiResponse(responseCode = "400", description = "인증 코드 불일치"),
	        @ApiResponse(responseCode = "400", description = "인증 실패 - 관리자에게 문의")
	        })
    public ResponseEntity<BaseResponseBody> checkJoinsEmailCode(@RequestBody MypageCheckJoinsEmailCodeReq mypageCheckJoinsEmailCodeReq, @ApiIgnore Principal principal) {
		log.info("MypageContoller > checkJoinsEmailCode - 호출 (userSeq : {})", principal.getName());
		
		Long userSeq = Long.parseLong(principal.getName());
		String certificationCode = mypageCheckJoinsEmailCodeReq.getCertificationCode();
		
		try {
			boolean answerStatus = mypageService.checkJoinsEmailCode(userSeq, certificationCode);
			if (answerStatus) {
				log.info("MypageContoller > checkJoinsEmailCode - 인증 성공 (userSeq : {})", userSeq);
				return ResponseEntity.status(201).body(BaseResponseBody.of(201, "임직원 인증 성공"));
			}
		} catch (BusinessException ex) {
			ErrorCode errorCode = ex.getErrorCode();
			if (errorCode == ErrorCode.TIME_OUT) {
				return ResponseEntity.status(400).body(BaseResponseBody.of(400, "인증 시간 만료"));
			} else if (errorCode == ErrorCode.WRONG_CERT_CODE) {
				return ResponseEntity.status(400).body(BaseResponseBody.of(400, "인증 코드 불일치"));
			}
		}
		
		log.error("MypageContoller > checkJoinsEmailCode - 인증 실패 (userSeq : {}, certificationCode : {})", userSeq, certificationCode);
		return ResponseEntity.status(400).body(BaseResponseBody.of(400, "인증 실패 - 관리자에게 문의"));
    }
}
