package com.contentree.interna.user.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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

import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.KakaoUtil;
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

/**
 * 
 * @author 이연희
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${spring.cookie.refresh-cookie-name}")
	private String refreshCookieName;

	@Value("${spring.cookie.access-cookie-name}")
	private String accessCookieName;

	@Value("${spring.security.jwt.refresh-token-expiration}")
	private Integer refreshTokenExpiration;

	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final RedisUtil redisUtil;
	private final CookieUtil cookieUtil;
	private final KakaoUtil kakaoUtil;

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
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);
		params.add("client_secret", clientSecret);

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
			log.error("UserService > findProfile - 잘못된 토큰");
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
		int month = Integer.parseInt(birth.substring(0, 2));
		int day = Integer.parseInt(birth.substring(2));
		cal.set(year, month - 1, day);
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
		redisUtil.setDataWithExpire(refreshToken, Long.toString(user.getUserSeq()), refreshTokenExpiration);
		log.info("UserService > SaveUserAndGetToken - redis에 저장 (refresh token : userSeq) = ({} : {})", refreshToken,
				user.getUserSeq());
		log.info("UserService > SaveUserAndGetToken - 토큰 저장 확인 {}:{}", redisUtil.getData(refreshToken));

		// controller로 전달
		SaveUserAndGetTokenRes userDto = SaveUserAndGetTokenRes.builder().userName(user.getUserName())
				.userGrade(user.getUserGrade()).userRole(user.getUserRole()).accessToken(accessToken)
				.refreshToken(refreshToken).build();

		return userDto;
	}

	public Boolean logout(Long userSeq) {
		// 카카오 로그아웃
		User user = userRepository.findById(userSeq).get();
		Long kakaoId = user.getUserKakaoId();
		if (kakaoId == null) {// TODO kakaoId가 null인 경우가 있나?
			log.error("UserService > logout - 카카오 로그아웃 실패");
			return false;
		}

		// TODO 카카오 서버 연결 해제
		kakaoUtil.logout(kakaoId);

		return true;
	}

	public boolean deleteToken(String refreshToken, String accessToken, HttpServletResponse response) {
		try {
			log.info("UserService > deleteToken - redis와 cookie에 있는 refresh 토큰 삭제 후 만료시간 0인 토큰 추가");
			// redis에 있는 refresh token 삭제
			redisUtil.deleteData(refreshToken);// key로 삭제

			// cookie에 있는 refresh token 삭제 후 response에 밀어넣기
			Cookie refreshCookie = cookieUtil.removeCookie(refreshCookieName);
			Cookie accessCookie = cookieUtil.removeCookie(accessCookieName);
			response.addCookie(refreshCookie);
			response.addCookie(accessCookie);

			// access token 블랙리스트 추가
			Integer tokenExpiration = jwtTokenUtil.getTokenExpirationAsInt(accessToken);
			redisUtil.setDataWithExpire(accessToken, "B", tokenExpiration);
		} catch (Exception e) {
			log.error("UserService > deleteToken - 토큰 로그인 설정 실패");
			return false;
		}
		return true;
	}
//
//	public String reissueToken(HttpServletRequest request, HttpServletResponse response) {
//
//		String refreshToken = cookieUtil.getCookie(request, refreshCookieName).getValue();
//		if (refreshToken != null) {
//			String stringUserSeq = redisUtil.getData(refreshToken);
//			if (stringUserSeq != null) {
//				log.info("UserService > reissueToken - refresh token으로 acess token 생성");
//				Long userSeq = Long.valueOf(stringUserSeq);
//				Optional<User> isUserPresent = userRepository.findById(userSeq);
//				if (isUserPresent.isPresent()) {
//					// 1.access token 생성
//					String newAccessToken = jwtTokenUtil.createAccessToken(userSeq);
//
//					// 2.redis에 기존 refresh token 삭제
//					redisUtil.deleteData(refreshToken);
//
//					// 3.refresh token 재발급
//					String newRefreshToken = jwtTokenUtil.createRefreshToken();
//
//					// 4.new refresh token 쿠키에 저장
//					Cookie newRefreshCookie = cookieUtil.createCookie(newAccessToken, newRefreshToken);
//					response.addCookie(newRefreshCookie);
//
//					// 5.new refresh token을 redis에 저장
//					redisUtil.setDataWithExpire(newRefreshToken, stringUserSeq, refreshTokenExpiration);
//
//					// 6.기존 accesstoken 블랙리스트
//					String originAccessToken = request.getHeader(jwtTokenUtil.HEADER_STRING)
//							.replace(jwtTokenUtil.TOKEN_PREFIX, "");
//					Integer tokenExpiration = jwtTokenUtil.getTokenExpirationAsLong(originAccessToken).intValue();
//
//					redisUtil.setDataWithExpire(originAccessToken, "B", tokenExpiration);
//
//					return newAccessToken;
//				} else {
//					return "DB";
//				}
//			} else {
//				return "EXP";
//			}
//		}
//		return null;
//	}

}
