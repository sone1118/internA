package com.contentree.interna.user.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.user.dto.SaveUserAndGetTokenRes;
import com.contentree.interna.user.dto.UserGetLoginRes;
import com.contentree.interna.user.oauth2.OauthToken;
import com.contentree.interna.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;
	private final CookieUtil cookieUtil;

	// 프론트에서 인가코드 돌려 받는 주소
	// 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	@GetMapping("/oauth/token")
	public ResponseEntity<UserGetLoginRes> getLogin(@RequestParam(value = "code") String code,
			HttpServletResponse httpServletResponse) throws IOException {
		log.info("UserController > getLogin - 인가코드로 토큰 발급, 사용자 정보와 토큰 저장");
		// 넘어온 인가 코드를 통해 access_token 발급
		OauthToken oauthToken = userService.getAccessToken(code);

		// 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
		SaveUserAndGetTokenRes sveAndGetTokenRes = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + sveAndGetTokenRes.getAccessToken());

		// access,refresh 토큰 레디스 저장, 쿠키 저장 전달
		// TODO 쿠키 생성 후 HttpServletResponse에 담아서 client로 전달
		Cookie refreshCookie = cookieUtil.createCookie("RefreshToken", sveAndGetTokenRes.getRefreshToken());
		Cookie accessCookie = cookieUtil.createCookie("AccessToken", sveAndGetTokenRes.getAccessToken());
		httpServletResponse.addCookie(refreshCookie);
		httpServletResponse.addCookie(accessCookie);

		UserGetLoginRes userGetLoginRes = UserGetLoginRes.builder().userName(sveAndGetTokenRes.getUserName())
				.userGrade(sveAndGetTokenRes.getUserGrade()).userRole(sveAndGetTokenRes.getUserRole()).build();

		// TODO 메인 페이지
		return ResponseEntity.ok().body(userGetLoginRes);
	}

}