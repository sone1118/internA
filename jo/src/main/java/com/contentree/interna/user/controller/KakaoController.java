package com.contentree.interna.user.controller;

import java.io.IOException;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nimbusds.jose.shaded.json.JSONObject;

@Controller
@RequestMapping("/api")
public class KakaoController {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String restApiKey;

	/**
	 * 인가코드 받기
	 * 
	 * @return 리다이렉트 url
	 */

	@GetMapping(value = "/kakao/oauth")
	public String kakaoConnect() {

		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("client_id=" + restApiKey);
		url.append("&redirect_uri=http://localhost:8080/kakao/callback");
		url.append("&response_type=code");

		return "redirect:/" + url.toString();
	}

	/**
	 * 받은 인가 코드를 사용해서 API에 토큰 발급 요청 보내기
	 * 
	 * @param code
	 * @param session
	 * @throws IOException
	 */

	@RequestMapping(value = "/kakao/callback")
	public void kakaoLogin(@RequestParam("code") String code, HttpSession session) throws IOException {
		String accessTokenString = getKakaoAccessToken(code);
		session.setAttribute("access_token", accessTokenString);// 로그아웃 할 때 사용됨
	}

	/**
	 * 
	 * @param code
	 * @return
	 */

	private String getKakaoAccessToken(String code) {
		// 카카오에 보낼 api
		WebClient webClient = WebClient.builder().baseUrl("https://kauth.kakao.com")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

		// 카카오 서버에 요청 보내기 & 응답 받기
		JSONObject response = webClient.post()
				.uri(uriBuilder -> uriBuilder.path("/oauth/token").queryParam("grant_type", "authorization_code")
						.queryParam("client_id", SecretKey.KAKAO_API_KEY)
						.queryParam("redirect_uri", Kakao.DOMAIN_URI + "/api/kakao/callback").queryParam("code", code)
						.build())
				.retrieve().bodyToMono(JSONObject.class).block();

		return (String) response.get("access_token");
	}
}