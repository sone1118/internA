package com.contentree.interna.user.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

/**
 * 
 * @author 이연희
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class UserController {

	private final UserService userService;
	private final CookieUtil cookieUtil;
	private final RedisUtil redisUtil;

	@Value("${spring.cookie.refresh-cookie-name}")
	private String refreshCookieName;

	@Value("${spring.cookie.access-cookie-name}")
	private String accessCookieName;

	// [ 이연희 ] 로그인, 회원가입, 토큰 발급 컨트롤러
	// 프론트에서 인가코드 돌려 받는 주소
	// 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	@Tag(name = "로그인페이지")
	@Operation(summary = "카카오 로그인 처리", description = "카카오 서버를 통해 인가코드와 토큰을 받아 유저 정보를 저장합니다.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "로그인 성공"),
			@ApiResponse(responseCode = "400", description = "로그인 실패") })
//	@GetMapping("/oauth/token")
	@GetMapping("/kakao/callback")
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

//	@GetMapping("/logout")
//	public String logout(HttpServletRequest request) {
//		String accessToken = 
//		return "";
//	}

}