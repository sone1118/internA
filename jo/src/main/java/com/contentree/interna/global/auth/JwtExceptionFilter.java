package com.contentree.interna.global.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (SignatureException ex) {
            log.error("JwtExceptionFilter - 유효하지 않은 JWT 서명. (JWT : {})", ex.getMessage());
            response.sendError(400, "올바르지 않은 요청입니다.");
        } catch (MalformedJwtException ex) {
            log.error("JwtExceptionFilter - 올바르지 않은 JWT 토큰 구조. (JWT : {})", ex.getMessage());
            response.sendError(400, "올바르지 않은 요청입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("JwtExceptionFilter - 지원하지 않는 형식의 JWT 토큰. (JWT : {})", ex.getMessage());
            response.sendError(400, "올바르지 않은 요청입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JwtExceptionFilter - 정보가 담겨있지 않은 빈 토큰. (JWT : {})", ex.getMessage());
            response.sendError(400, "올바르지 않은 요청입니다.");
		} catch (BusinessException ex) {
			ErrorCode errorCode = ex.getErrorCode();
			String errorMessage = ex.getMessage();
			if (errorCode == ErrorCode.BLACK_REFRESH) {
				log.error("JwtExceptionFilter - 사용할 수 없는 Refresh Token (refresh token : {})", errorMessage);
			} else if (errorCode == ErrorCode.BLACK_ACCESS) {
				log.error("JwtExceptionFilter - 사용할 수 없는 Access Token (access token : {})", errorMessage);
			} else if (errorCode == ErrorCode.CREATOR_ERROR) {
				log.error("JwtExceptionFilter - Refresh Token을 만든 userSeq와 Access Token에 담긴 userSeq가 불일치 ({})", errorMessage);
			} else if (errorCode == ErrorCode.NONEXISTENT_USER) {
				log.error("JwtExceptionFilter - 존재하지 않는 userSeq (userSeq : {})", errorMessage);
			}
			response.sendError(400, "올바르지 않은 요청입니다.");
		}
    }
}