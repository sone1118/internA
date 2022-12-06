package com.contentree.interna.global.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.contentree.interna.global.model.BusinessException;
import com.contentree.interna.global.model.ErrorCode;
import com.contentree.interna.global.util.CookieUtil;
import com.contentree.interna.global.util.JwtTokenUtil;
import com.contentree.interna.global.util.RedisUtil;
import com.contentree.interna.user.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Value("${spring.security.password-secret-key}")
    private String passwordSecretKey;

    @Value("${spring.cookie.refresh-cookie-name}")
    private String refreshCookieName;
    
    @Value("${spring.cookie.access-cookie-name}")
    private String accessCookieName;
    
    @Value("${spring.security.jwt.refresh-token-expiration}")
    private int refreshTokenExpiration;

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final JwtTokenUtil jwtTokenUtil;
    
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String refreshToken = null;
        String accessToken = null;
        String refreshUserSeq = null;
        
    	try {
	    	// < Refresh Token 검사 >
	    	// 1. Refresh Token이 담긴 쿠키 가져오기 
	        Cookie refreshCookie = cookieUtil.getCookie(request, refreshCookieName);

	        // 1-1. Refresh Cookie가 존재할 경우 (존재하지 않으면 authentcatioEntryPoint에서 처리)
	        if (refreshCookie != null) {
	        	refreshToken = refreshCookie.getValue();
	        	
	        	// 2. Refresh Token 유효성 검사 
	        	if (refreshToken != null) {	// 쿠키의 value 값 있는 경우 
	        		jwtTokenUtil.validateToken(refreshToken);
	        	}
	        	
	        	// 3. Redis에 존재하는지 확인 (Refresh Token이 블랙리스트 처리 되었는지 확인)
	        	refreshUserSeq = redisUtil.getData(refreshToken);
	        	// 3-1. 블랙리스트 처리 된 경우
	        	if (refreshUserSeq == null) {
	        		throw new BusinessException(ErrorCode.BLACK_REFRESH, refreshToken);
	        	}
	        
		        // < Access Token 검사 >
		        // 1. Access Token이 담긴 쿠키 가져오기 
		        Cookie accessCookie = cookieUtil.getCookie(request, accessCookieName);
		        
		        // 1-1. Access Cookie가 존재할 경우
		        if (accessCookie != null) {
		        	accessToken = accessCookie.getValue();
		        
			        // 2. Access Token 가져오기 
		        	// 2-1. Access Token 존재하는 경우 
			        if (accessToken != null) {	
			        	// 3. Access Token 유효성 검사
			        	jwtTokenUtil.validateToken(accessToken);
			        	
			            // 4. Access Token이 이미 재발급 되어서 redis에 블랙리스트로 들어가있는지 확인
			            String inBlackList = redisUtil.getData(accessToken.replace(jwtTokenUtil.TOKEN_PREFIX, ""));
			            // 4-1. 블랙리스트 처리 되어있는 경우
			        	if (inBlackList != null && inBlackList.equals("B")) {
			                throw new BusinessException(ErrorCode.BLACK_ACCESS, accessToken);
			            }
			            
		                // 5. Access Token에서 사용자 정보 추출
		                Long accessUserSeq = jwtTokenUtil.getUserSeq(accessToken);
		                
		                // 5-1. redis에 저장되어있는 유저 정보와 토큰에서 가져온 유저 정보가 다를 경우
		                if (Long.parseLong(refreshUserSeq) != accessUserSeq) {
		                	StringBuilder builder = new StringBuilder();
		                	builder.append("Refresh Token UserSeq : ");
		                	builder.append(refreshUserSeq);
		                	builder.append(", Access Token UserSeq : ");
		                	builder.append(accessUserSeq);
		                	throw new BusinessException(ErrorCode.CREATOR_ERROR, builder.toString());
		                }
		                
		                // 5-2. 토큰에서 유저 정보를 받아오지 못했을 경우
		                if (accessUserSeq == null) throw new IllegalArgumentException();
		
		                // 6. Access Token 토큰에 포함된 유저 정보를 통해 실제 DB에 해당 정보의 계정이 있는지 조회
		                User user = customUserDetailsService.getUserDetail(accessUserSeq);
		                if (user != null) {
		                    // 8. security 인증 객체 생성 
		                    createJwtAuthentication(accessUserSeq, user);
		                } else { // 5-1. DB에 해당 유저 없는 경우
		                    throw new BusinessException(ErrorCode.NONEXISTENT_USER, refreshUserSeq);
		                }
			        }
		        } else { // 1-2. Access Cookie 존재하지 않는 경우 (쿠키 만료) => 재발급
		        	reissueToken(response, refreshToken, Long.parseLong(refreshUserSeq));
		        }
	        }
        } catch (SignatureException ex) {
            throw new SignatureException(ex.getMessage());
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException(ex.getMessage());
        } catch (ExpiredJwtException ex) {
        	// Refresh Token이 유효할 경우에만 토큰 재발급 (Refresh Token 만료된 경우는 재로그인)
        	if (refreshToken != null && refreshUserSeq != null) {
        		reissueToken(response, refreshToken, Long.parseLong(refreshUserSeq));
        	}
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    	filterChain.doFilter(request, response);
    }
    
    // 토큰 재발급 및 인증 객체 생성 
    private HttpServletResponse reissueToken(HttpServletResponse response, String oldRefreshToken, Long userSeq) {
    	log.info("JwtAuthenticationFilter - reissueToken 호출, (userSeq : {})", userSeq);
    	
    	// 1. Refresh Token 삭제 (블랙 처리)
    	redisUtil.deleteData(oldRefreshToken);
    	
    	// 2. 토큰 새로 발급
    	String newAccessToken = jwtTokenUtil.createAccessToken(userSeq);
    	String newRefreshToken = jwtTokenUtil.createRefreshToken();
    	
    	// 3. 새 Refresh Token을 Redis 저장
    	redisUtil.setDataWithExpire(newRefreshToken, userSeq.toString(), refreshTokenExpiration);
    	
    	// 4. 새 토큰 쿠키에 저장
    	response.addCookie(cookieUtil.createCookie(accessCookieName, newAccessToken));
    	response.addCookie(cookieUtil.createCookie(refreshCookieName, newRefreshToken));
    	
    	// 5. 유저 정보 가져오기 
    	User user = customUserDetailsService.getUserDetail(userSeq);
    	// 5-1. 유저 정보 없는 경우 
    	if (user == null) throw new BusinessException(ErrorCode.NONEXISTENT_USER, userSeq.toString());
    	
    	// 6. security 인증 객체 생성
    	createJwtAuthentication(userSeq, user);
    	
    	return response;
    }
    
    // Security 인증 객체 생성 
    private void createJwtAuthentication(Long userSeq, User user) {
    	// 1. 요청 context 내에서 참조 가능한 인증 정보(jwtAuthentication) 생성
        UsernamePasswordAuthenticationToken jwtAuthentication = new UsernamePasswordAuthenticationToken(userSeq,
                userSeq + passwordSecretKey, AuthorityUtils.createAuthorityList(user.getUserRole().name()));

        // 2. jwt 토큰으로 부터 획득한 인증 정보(authentication) 설정
        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
    }
}
