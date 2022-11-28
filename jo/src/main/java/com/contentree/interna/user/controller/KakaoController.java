package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kakao")
public class KakaoController {
	private KakaoLogin kakao_restapi = new KakaoLogin();

	@GetMapping(value = "/oauth")
	public String kakaoConnect() {

		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("client_id=" + "클라이언트에 등록된 client_id");
		url.append("&redirect_uri=리다이렉트될 url");
		url.append("&response_type=code");

		return "redirect:" + url.toString();
	}
}