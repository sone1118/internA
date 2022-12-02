package com.contentree.interna.user.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.dto.KakaoProfile;
import com.contentree.interna.user.dto.OauthTokenDto;
import com.contentree.interna.user.dto.SaveUserAndGetTokenRes;
import com.contentree.interna.user.entity.Grade;
import com.contentree.interna.user.entity.Role;
import com.contentree.interna.user.entity.User;
import com.contentree.interna.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisUtil redisUtil;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	String client_id;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	String client_secret;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	String redirect_uri;

	public OauthTokenDto getAccessToken(String code) {
		log.info("UserService > getAccessToken - 인가코드 값으로 Token 생성");
		// POST 방식으로 key=value 데이터 요청
		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", client_id);
		params.add("redirect_uri", redirect_uri);
		params.add("code", code);
		params.add("client_secret", client_secret);

		// HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		// Http 요청 (POST 방식) 후, response 변수의 응답을 받음
		try {
			ResponseEntity<String> accessTokenResponse = rt.exchange("https://kauth.kakao.com/oauth/token",
					HttpMethod.POST, kakaoTokenRequest, String.class);

			// JSON 응답을 객체로 변환
			ObjectMapper objectMapper = new ObjectMapper();
			OauthTokenDto oauthToken = null;
			oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthTokenDto.class);
			return oauthToken;
		} catch (JsonProcessingException e) {
			log.error("UserService > getAccessToken - Json 파싱 실패");
			return null;
		} catch (HttpClientErrorException e) {
			log.error("UserService > getAccessToken - 잘못된 인가코드");
			return null;
		}

	}

	public KakaoProfile findProfile(String token) {
		log.info("UserService > findProfile - KakaoProfile 형식에 맞는 객체 반환");

		// POST 방식으로 key=value 데이터 요청
		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

		try {
			// Http 요청 (POST 방식) 후, response 변수의 응답을 받음
			ResponseEntity<String> kakaoProfileResponse = rt.exchange("https://kapi.kakao.com/v2/user/me",
					HttpMethod.POST, kakaoProfileRequest, String.class);

			// JSON 응답을 객체로 변환
			ObjectMapper objectMapper = new ObjectMapper();
			KakaoProfile kakaoProfile = null;

			kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
			return kakaoProfile;
		} catch (JsonProcessingException e) {
			log.error("UserService > findProfile - Json 파싱 실패");
			return null;
		} catch (HttpClientErrorException e) {
			log.error("UserService > findProfile - 잘못된 인가코드");
			return null;
		}
	}

	public SaveUserAndGetTokenRes SaveUserAndGetToken(String token) {
		log.info(
				"UserService > SaveUserAndGetToken - token을 사용히여 유저를 조회 후 db에 저장. accessToken, refreshToken을 생성하고 redis에 저장 후 userDto 객체 반환.");
		KakaoProfile profile = findProfile(token);

		Calendar cal = Calendar.getInstance();
		int year = 1998;
		String birth = profile.getKakao_account().getBirthday();
		int month = Integer.parseInt(birth.substring(0, 3));
		int day = Integer.parseInt(birth.substring(2));
		cal.set(year, month, day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

		String prefixPhone = "0101234";
		String userPhone = prefixPhone + birth;
		User user = userRepository.findByUserPhone(userPhone);// 0101234 + 0122

		if (user == null) {
			user = User.builder().userName(profile.getKakao_account().getProfile().getNickname())
					.userEmail(profile.getKakao_account().getEmail()).userPhone(userPhone).userBirth(cal)
					.userKakaoId(profile.getId()).userRole(Role.ROLE_USER).userGrade(Grade.BRONZE)
					.userAgreeMarketing(true).userAgreeMarketing(true).build();
			userRepository.save(user);
		} else if (!user.getUserEmail().equals(profile.getKakao_account().getEmail())) {// 중복가입
			log.error(user.getUserEmail());
			log.error(profile.getKakao_account().getEmail());
			log.error("UserService > SaveUserAndGetToken - 유저정보가 데이터베이스에 이미 존재");
			return null;
		}

		// access token, refresh token 생성
		String accessToken = jwtTokenUtil.createAccessToken(user.getUserSeq());
		String refreshToken = jwtTokenUtil.createRefreshToken();

		// redis에 {refresh:userSeq} 저장
		redisUtil.setDataWithExpire(refreshToken, Long.toString(user.getUserSeq()), 10000);
		log.info("save data to redis (refresh token : userSeq) = ({} : {})", refreshToken, user.getUserSeq());
		log.info("######토큰 저장 확인 {}:{}######", redisUtil.getData(refreshToken));

		// controller로 전달
		SaveUserAndGetTokenRes userDto = SaveUserAndGetTokenRes.builder().userName(user.getUserName())
				.userGrade(user.getUserGrade()).userRole(user.getUserRole()).accessToken(accessToken)
				.refreshToken(refreshToken).build();

		return userDto;
	}

	public String logout() {
		return "";
	}
}
