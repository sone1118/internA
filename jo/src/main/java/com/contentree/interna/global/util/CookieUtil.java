package com.contentree.interna.global.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CookieUtil {

	@Value("${spring.security.jwt.access-token-expiration}")
	private int accessTokenExpiration;
	
	@Value("${spring.security.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    public Cookie createCookie(String cookieName, String value){
    	log.info("CookieUtil > createCookie - 호출 (구울 쿠키 이름 : {}, 값 : {}", cookieName, value);
    	
    	int expiration;
    	if (cookieName.charAt(0) == 'a') {
    		expiration = accessTokenExpiration / 1000;
    	} else {
    		expiration = refreshTokenExpiration / 1000;
    	}
    	
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiration);
        cookie.setPath("/");
        
        return cookie;
    }

    public Cookie getCookie(HttpServletRequest request, String cookieName){
    	log.info("CookieUtil > getCookie - 호출 (가져올 쿠키 이름 : {}", cookieName);
        final Cookie[] cookies = request.getCookies();
        if(cookies == null) {
        	log.error("CookieUtil > getCookie - 존재하는 쿠키가 없음 (쿠키 목록 null) (가져올 쿠키 이름 : {})", cookieName);
        	return null;}
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName)) return cookie;    
        }
        log.error("CookieUtil > getCookie - 해당 이름의 쿠키가 없음 (가져올 쿠키 이름 : {})", cookieName);
        return null;
    }

    public Cookie removeCookie(String cookieName){
    	log.info("CookieUtil > removeCookie - 호출 (삭제할 쿠키 이름 : {}", cookieName);
        Cookie expiredCookie = new Cookie(cookieName, null);
        expiredCookie.setMaxAge(0); // expiration 타임 0으로 하여 삭제
        expiredCookie.setPath("/"); // 모든 경로에서 삭제
        return expiredCookie;
    }
}
