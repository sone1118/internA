package com.contentree.interna.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	// TODO OauthToken, UserService 개발
	// 프론트에서 인가코드 받아오는 url
	@GetMapping("/oauth/token")
	public User getLogin(@RequestParam("code") String code) {
		// 넘어온 인가 코드를 통해 access_token 발급
		OauthToken oauthToken = userService.getAccessToken(code);

		// 1) 발급 받은 accessToken으로 카카오 회원 정보 DB 저장
		String user = userService.saveUser(oauthToken.getAccess_token());

		return user;
	}

}