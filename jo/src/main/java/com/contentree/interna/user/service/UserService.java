package com.contentree.interna.user.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.KakaoProfile;
import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.oauth2.OauthToken;
import com.contentree.interna.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	public static final String FRONT_URL = "http://localhost:8080";
	private final JwtTokenUtil jwtTokenUtil;

	@Value("${spring.security.client.registration.kakao.client-id}")
	String client_id;

	@Value("${spring.security.client.registration.kakao.client-secret}")
	String client_secret;

	public OauthToken getAccessToken(String code) throws IOException {

		// POST 방식으로 key=value 데이터 요청
		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", client_id);
		params.add("redirect_uri", FRONT_URL + "/auth");
		params.add("code", code);
		params.add("client_secret", client_secret);

		// HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		// Http 요청 (POST 방식) 후, response 변수의 응답을 받음
		ResponseEntity<String> accessTokenResponse = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoTokenRequest, String.class);

		// JSON 응답을 객체로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		OauthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return oauthToken;
	}

	public KakaoProfile findProfile(String token) throws IOException {
		// POST 방식으로 key=value 데이터 요청
		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

		// Http 요청 (POST 방식) 후, response 변수의 응답을 받음
		ResponseEntity<String> kakaoProfileResponse = rt.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfileRequest, String.class);

		// JSON 응답을 객체로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
			System.out.println("=================================================");
			System.out.println(kakaoProfileResponse.getBody());
			System.out.println("=================================================");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return kakaoProfile;
	}

	public User getUser(HttpServletRequest request) {
		Long userSeq = (Long) request.getAttribute("userSeq");

		User user = userRepository.findByUserSeq(userSeq);

		return user;
	}

	public String SaveUserAndGetToken(String token) throws IOException {
		KakaoProfile profile = findProfile(token);

		User user = userRepository.findByUserEmail(profile.getKakao_account().getEmail());
		if (user == null) {// TODO 하드 코딩 해둠 변경 필요
			user = User.builder()// TODO .userSeq(profile.getId()) userSeq도 설정해줘야 하나?
					.userName("EXUSERNAME").userEmail(profile.getKakao_account().getEmail()).userPhone("01012345678")
					.userKakaoId(profile.getId()).userRole(Role.JOINS).userGrade(Grade.BRONZE).userAgreeMarketing(true)
					.userAgreeMarketing(true).build();

			userRepository.save(user);
		}

		return jwtTokenUtil.createAccessToken(user.getUserSeq());// access token
	}

}
