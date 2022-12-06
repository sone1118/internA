package com.contentree.interna.user.controller;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contentree.interna.global.model.BaseResponseBody;
import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.dto.OauthTokenDto;
import com.contentree.interna.user.dto.SaveUserAndGetTokenRes;
import com.contentree.interna.user.dto.UserGetLoginRes;
import com.contentree.interna.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author 이연희
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="회원 관리", description = "회원 관리 API")
public class UserController {
	
	@Value("${spring.cookie.refresh-cookie-name}")
	private String refreshCookieName;

	@Value("${spring.cookie.access-cookie-name}")
	private String accessCookieName;

	private final UserService userService;
	
	private final CookieUtil cookieUtil;
	private final RedisUtil redisUtil;

	// [ 이연희 ] 로그인, 회원가입, 토큰 발급 컨트롤러
	// 프론트에서 인가코드 돌려 받는 주소
	// 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	@Tag(name = "로그인페이지")
	@Operation(summary = "카카오 로그인 처리", description = "카카오 서버를 통해 인가코드와 토큰을 받아 유저 정보를 저장합니다.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "로그인 성공"),
			@ApiResponse(responseCode = "400", description = "로그인 실패") })
	@GetMapping("/oauth/token")
	public ResponseEntity<UserGetLoginRes> getLogin(@RequestParam(value = "code") String code,
			HttpServletResponse httpServletResponse) {
		log.info("UserController > getLogin - 인가코드로 토큰 발급, 사용자 정보와 토큰 저장");
		// 넘어온 인가 코드를 통해 access_token 발급

		OauthTokenDto oauthToken = userService.getAccessToken(code);

		if (oauthToken == null) {
			log.error("UserController > getLogin - 토큰 생성 실패");
			return ResponseEntity.status(400).build();
		}

		// 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
		SaveUserAndGetTokenRes saveAndGetTokenRes = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

		if (saveAndGetTokenRes == null) {
			log.info("UserController > getLogin - 중복가입");
			return ResponseEntity.status(400).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + saveAndGetTokenRes.getAccessToken());

		// access,refresh 토큰 레디스 저장, 쿠키 저장 전달
		// 쿠키 생성 후 HttpServletResponse에 담아서 client로 전달
		Cookie refreshCookie = cookieUtil.createCookie(refreshCookieName, saveAndGetTokenRes.getRefreshToken());
		Cookie accessCookie = cookieUtil.createCookie(accessCookieName, saveAndGetTokenRes.getAccessToken());
		httpServletResponse.addCookie(refreshCookie);
		httpServletResponse.addCookie(accessCookie);

		UserGetLoginRes userGetLoginRes = UserGetLoginRes.builder().userName(saveAndGetTokenRes.getUserName())
				.userGrade(saveAndGetTokenRes.getUserGrade()).userRole(saveAndGetTokenRes.getUserRole()).build();

		return ResponseEntity.ok().body(userGetLoginRes);
	}
	
	
	// [ 김지슬 ] 회원 탈퇴 
	@Tag(name="회원 관리")
	@DeleteMapping("/users")
	@Operation(summary = "회원 탈퇴", description = "회원 정보를 회원 탈퇴 테이블로 넘깁니다.")
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
	        @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패 - 관리자 확인 필요")
	        })
	public ResponseEntity<BaseResponseBody> removeUser(HttpServletRequest request, HttpServletResponse response, @ApiIgnore Principal principal) {
		log.info("UserContoller > removeUser - 호출 (userSeq : {})", principal.getName());
		Long userSeq = Long.parseLong(principal.getName());

		// 1. 유저 정보 삭제 
		boolean answerStatus = userService.removeUser(userSeq);
		if (answerStatus) {
			// 2. 토큰 블랙리스트 처리 
			// 2-1. 각 토큰 쿠키에서 가져오기 (쿠키 및 쿠키 value의 null 처리는 jwtAuthenticationFilter에서 검증되었으므로 해당 과정 X)
			String refreshToken = cookieUtil.getCookie(request, refreshCookieName).getValue();
			String accessToken = cookieUtil.getCookie(request, accessCookieName).getValue();
			
			// 2-2. 각 토큰 블랙리스트 처리
			userService.blackToken(refreshToken, accessToken);
			
			// 3. 쿠키 삭제
			Cookie refreshDelCookie = cookieUtil.removeCookie(refreshCookieName);
			Cookie accessDelCookie = cookieUtil.removeCookie(accessCookieName);
			
			response.addCookie(refreshDelCookie);
			response.addCookie(accessDelCookie);
			
			log.info("UserContoller > removeUser - 회원 탈퇴 성공 (userSeq : {})", userSeq);
			return ResponseEntity.status(200).body(BaseResponseBody.of(200, "회원 탈퇴 성공"));
		}
		
		log.info("UserContoller > removeUser - 회원 탈퇴 실패 (userSeq : {})", userSeq);
		return ResponseEntity.status(400).body(BaseResponseBody.of(400, "회원 탈퇴 실패 - 관리자 확인 필요"));
	}
}