package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/kakao")
public class KakaoController {
	// 인가코드 받기 위해 카카오 서버와 connect
	@GetMapping(value = "/oauth")
	public String kakaoConnect() {
		log.info("KakaoController > kakaoConnect - 인가코드를 받기 위해 카카오 서버와 연결");
		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("client_id=" + "29010893dc4173be214f7ca77ce327d1");
		url.append("&redirect_uri=http://localhost:8080/kakao/callback");
		url.append("&response_type=code");

		return "redirect:" + url.toString();
	}
}