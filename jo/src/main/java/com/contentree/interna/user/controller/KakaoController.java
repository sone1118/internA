package com.contentree.interna.user.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.oauth2.OauthToken;
import com.contentree.interna.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class KakaoController {

	private final UserService userService;

	// 프론트에서 인가코드 돌려 받는 주소
	// 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	@GetMapping("/oauth/token")
	public ResponseEntity getLogin(@RequestParam("code") String code) throws IOException {

		// 넘어온 인가 코드를 통해 access_token 발급
		OauthToken oauthToken = userService.getAccessToken(code);

		// 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
		String jwtToken = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + jwtToken);

		return ResponseEntity.ok().headers(headers).body("success");
	}

	// jwt 토큰으로 유저정보 요청하기
	@GetMapping("/me")
	public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) {

		User user = userService.getUser(request);

		return ResponseEntity.ok().body(user);
	}
}