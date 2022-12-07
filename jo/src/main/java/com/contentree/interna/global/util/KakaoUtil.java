package com.contentree.interna.global.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KakaoUtil {
	@Value("${kakao.admin-key}")
	private String adminKey;

	@Value("${kakao.logout-redirect-uri}")
	private String logoutRedirectUri;

	public Long unlinkUser(Long userKakaoId) {
		log.info("KakaoUtil > unlinkUser - 호출 (userKakaoId : {})", userKakaoId);
		
		// 카카오에 요청 보내기
		ResponseEntity<String> response = requestToKakaoByAK(userKakaoId, "v1/user/unlink");
		log.info("KakaoUtil > unlinkUser - 요청 성공 (userKakaoId : {})", userKakaoId);
		
		// 응답받은 id값 추출
		JSONObject body = new JSONObject(response.getBody());
		Long resKakaoId = body.getLong("id");
		log.info("KakaoUtil > unlinkUser - 카카오에서 응답 받은 KakaoId:{}", resKakaoId);
		
		return resKakaoId;
	}
		
	public Long logout(Long userKakaoId) {
		log.info("KakaoUtil > logout - 카카오 로그인 연결 해제 (userKakaoId : {})", userKakaoId);
		// 카카오에 요청 보내기
		ResponseEntity<String> response = requestToKakaoByAK(userKakaoId, "v1/user/logout");

		// 응답받은 id값 가져오기
		JSONObject body = new JSONObject(response.getBody());
		Long resKakaoId = body.getLong("id");

		log.info("KakaoUtil > logout - 카카오에서 응답 받은 KakaoId:{}", resKakaoId);

		return resKakaoId;
	}

	private ResponseEntity<String> requestToKakaoByAK(Long userKakaoId, String requestUrl) {
		log.info("KakaoUtil > requestToKakaoByAK - 호출 (userKakaoId : {}, requestUrl : {})", userKakaoId, requestUrl);
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "KakaoAK " + adminKey);

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("target_id", String.valueOf(userKakaoId));
		params.add("target_id_type", "user_id");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		RestTemplate rt = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> kakaoAKRequest = new HttpEntity<>(params, headers);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response = rt.exchange("https://kapi.kakao.com/" + requestUrl, HttpMethod.POST,
				kakaoAKRequest, String.class);

		return response;
	}
}
