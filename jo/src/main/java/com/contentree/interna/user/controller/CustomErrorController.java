package com.contentree.interna.user.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import com.contentree.interna.global.util.CookieUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController{
	
	@Value("${spring.cookie.refresh-cookie-name}")
    private String refreshCookieName;
    
    @Value("${spring.cookie.access-cookie-name}")
    private String accessCookieName;
    
	private final CookieUtil cookieUtil;

	
	@ExceptionHandler(Throwable.class)
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String statusMsg = status.toString();
        model.addAttribute("code", statusMsg);
        
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        if (messageObj != null) {
        	String message = messageObj.toString();
        	if (!"".equals(message) && "AUTH_001".equals(message.substring(1, 9))) {
        		Cookie removeRefreshCookie = cookieUtil.removeCookie(refreshCookieName);
            	Cookie removeAccessCookie = cookieUtil.removeCookie(accessCookieName);
            	
            	response.addCookie(removeRefreshCookie);
            	response.addCookie(removeAccessCookie);
            	log.error("CustomErrorController > handleError - 잘못된 토큰. 토큰 쿠키 삭제");
        	}
        	model.addAttribute("msg", message);
        	log.info("CustomErrorController > handleError - 호출 (코드 : {}, 에러 메시지: {})", statusMsg, message);
        } else {
        	HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(statusMsg));
        	model.addAttribute("msg", httpStatus);
        	log.info("CustomErrorController > handleError - 호출 (코드 : {}, 에러 메시지: {})", statusMsg, httpStatus);
        }

        return "error";
    }
}
