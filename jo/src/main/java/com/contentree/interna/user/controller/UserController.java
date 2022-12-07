package com.contentree.interna.user.controller;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.user.dto.OauthTokenDto;
import com.contentree.interna.user.dto.SaveUserAndGetTokenRes;
import com.contentree.interna.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author 이연희
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final CookieUtil cookieUtil;

	@Value("${spring.cookie.refresh-cookie-name}")
	private String refreshCookieName;

	@Value("${spring.cookie.access-cookie-name}")
	private String accessCookieName;

	// [ 이연희 ] 로그인, 회원가입, 토큰 발급 컨트롤러
	// 프론트에서 인가코드 돌려 받는 주소
	// 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
	@Tag(name = "회원 관리")
	@Operation(summary = "카카오 로그인 처리", description = "카카오 서버를 통해 인가코드와 토큰을 받아 유저 정보를 저장합니다.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "로그인 성공"),
			@ApiResponse(responseCode = "400", description = "로그인 실패") })
	@GetMapping("/kakao/callback")
	public String login(@RequestParam(value = "code") String code, HttpServletResponse httpServletResponse,
			RedirectAttributes redirect) {
		log.info("UserController > getLogin - 인가코드로 토큰 발급, 사용자 정보와 토큰 저장");
		// 넘어온 인가 코드를 통해 access_token 발급

		// TODO String으로 error 보내기 ->타임리프

		OauthTokenDto oauthToken = userService.getAccessToken(code);

		if (oauthToken == null) {
			log.error("UserController > getLogin - 토큰 생성 실패");
			redirect.addAttribute("error", "로그인에 실패했습니다.");
			return "redirect:/";
		}

		// 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
		SaveUserAndGetTokenRes saveAndGetTokenRes = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

		if (saveAndGetTokenRes == null) {
			log.info("UserController > getLogin - 중복가입");
			redirect.addAttribute("error", "로그인에 실패했습니다.");
			return "redirect:/";
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + saveAndGetTokenRes.getAccessToken());

		// access,refresh 토큰 레디스 저장, 쿠키 저장 전달
		// 쿠키 생성 후 HttpServletResponse에 담아서 client로 전달
		Cookie refreshCookie = cookieUtil.createCookie(refreshCookieName, saveAndGetTokenRes.getRefreshToken());
		Cookie accessCookie = cookieUtil.createCookie(accessCookieName, saveAndGetTokenRes.getAccessToken());
		httpServletResponse.addCookie(refreshCookie);
		httpServletResponse.addCookie(accessCookie);

		return "redirect:/";
	}

	@GetMapping("/api/users/logout")
	@Tag(name = "회원 관리")
	@Operation(summary = "로그아웃", description = "카카오톡 로그아웃")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
			@ApiResponse(responseCode = "400", description = "로그아웃 실패") })
	public String logout(@ApiIgnore Principal principal, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirect) {
		log.info("UserController > logout - logout 시작");

		Long userSeq = Long.valueOf(principal.getName());
		log.info("UserController > logout - userSeq={}", userSeq);

		Boolean logoutComplete = userService.logout(userSeq);
		log.info("UserController > logout - logoutComplete={}", logoutComplete);
		if (logoutComplete == false) {
			log.error("UserController > logout - 로그아웃 실패");
			redirect.addAttribute("error", "로그아웃에 실패했습니다.");
			return "redirect:/";
		}

		String refreshToken = cookieUtil.getCookie(request, refreshCookieName).getValue();
		String accessToken = cookieUtil.getCookie(request, accessCookieName).getValue();
		log.info("UserController > logout - refresh:{}, access:{}", refreshToken, accessToken);
		boolean deleteToken = userService.deleteToken(refreshToken, accessToken, response);
		log.info("UserController > logout - deleteToken={}", deleteToken);
		if (!deleteToken) {
			log.error("UserController > logout - 토큰 삭제 실패");
			redirect.addAttribute("error", "로그아웃에 실패했습니다.");
			return "redirect:/";
		}

		return "redirect:/";
	}

//	@GetMapping("/refresh")
//	@Tag(name = "회원 관리")
//	@Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급")
//	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공"),
//			@ApiResponse(responseCode = "400", description = "Refresh Token 없거나 존재하지 않는 사용자로 Refresh Token 재발급 실패"),
//			@ApiResponse(responseCode = "401", description = "만료된 Refresh Token") })
//	public ResponseEntity<LoginRes> reissueToken(HttpServletRequest request, HttpServletResponse response) {
//		log.info("reissueToken - 호출");
//
//		String accessToken = userService.reissueToken(request, response);
//		if (accessToken == null) {
//			log.error("reissueToken - Refresh Token이 없습니다.");
//			return ResponseEntity.status(400).body(LoginRes.of(400, "Refresh Token이 없습니다.", null));
//		} else if (accessToken.equals("DB")) {
//			log.error("reissueToken - 존재하지 않는 사용자");
//			return ResponseEntity.status(400).body(LoginRes.of(400, "존재하지 않는 사용자입니다.", null));
//		} else if (accessToken.equals("EXP")) {
//			log.error("reissueToken - 잘못되거나 만료된 Refresh Token");
//			return ResponseEntity.status(401).body(LoginRes.of(401, "잘못되거나 만료된 Refresh Token입니다.", null));
//		}
//		return ResponseEntity.status(200).body(LoginRes.of(200, "Success", accessToken));
//	}

}