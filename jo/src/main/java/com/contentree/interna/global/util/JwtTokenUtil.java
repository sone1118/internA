package com.contentree.interna.global.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Slf4j
@Component
public class JwtTokenUtil {
    private final Key key;

    private final int accessTokenExpiration;
    private final int refreshTokenExpiration;

    public final String TOKEN_PREFIX = "Bearer ";
//    public final String HEADER_STRING = "Authorization";

    @Autowired
    public JwtTokenUtil(@Value("${spring.security.jwt.secret}") String secretKey,
                        @Value("${spring.security.jwt.access-token-expiration}") int accessTokenExpiration,
                        @Value("${spring.security.jwt.refresh-token-expiration}") int refreshTokenExpiration) {

        // secretKey 바이트로 변환하여 Base64로 인코딩
        String encodingSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        // Base64 byte[]로 변환
        byte[] decodedByte = Base64.getDecoder().decode(encodingSecretKey.getBytes(StandardCharsets.UTF_8));
        // byte[]로 key 생성
        this.key = Keys.hmacShaKeyFor(decodedByte);

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token 생성
    public String createAccessToken(long userSeq) {
    	log.info("JwtTokenUtil > createAccessToken - 호출 (userSeq : {})", userSeq);
    	
        Date expires = JwtTokenUtil.getTokenExpirationAsDate(accessTokenExpiration);
        String jwts = Jwts.builder()
                .setSubject("Access Token")
                .claim("userSeq", userSeq)  // userSeq 저장
                .setExpiration(expires) // 만료시간
                .setIssuer("Jo")    // 발행자
                .signWith(key, SignatureAlgorithm.HS512)    // 암호화
                .compact();
        
        log.info("JwtTokenUtil > createAccessToken - Access Token 생성 완료 (Access Token : {})", jwts);
        
        return jwts;
    }

    // Refresh Token 생성
    public String createRefreshToken() {
    	log.info("JwtTokenUtil > createRefreshToken - 호출");
    	
        Date expires = JwtTokenUtil.getTokenExpirationAsDate(refreshTokenExpiration);
        String jwts = Jwts.builder()
                .setSubject("Refresh Token")
                .setExpiration(expires) // 만료시간
                .setIssuer("Jo")    // 발행자
                .signWith(key, SignatureAlgorithm.HS512)    // 암호화
                .compact();
        
        log.info("JwtTokenUtil > createRefreshToken - Refresh Token 생성 완료 (Refresh Token : {})", jwts);
        
        return jwts;
    }

    // 토큰에 담긴 payload 값 가져오기
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
    	 log.info("JwtTokenUtil > extractAllClaims - 호출 (token : {})", token);
    	 
//        String tokenDelPrefix = token.replace(TOKEN_PREFIX, "");
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 userSeq 가져오기
    public Long getUserSeq(String token) {
    	log.info("JwtTokenUtil > getUserSeq - 호출 (token : {})", token);
    	
        return extractAllClaims(token).get("userSeq", Long.class);
    }
    
    // 토큰 만료 날짜 가져오기
    public static Date getTokenExpirationAsDate(Integer expirationTime) {
    	log.info("JwtTokenUtil > getTokenExpirationAsDate - 호출 (expirationTime : {})", expirationTime);
    	
        Date now = new Date();
        return new Date(now.getTime() + expirationTime);
    }
    
    // 토큰 만료 시간 가져오기
    public int getTokenExpirationAsInt(String token) {
    	log.info("JwtTokenUtil > getTokenExpirationAsInt - 호출 (token : {})", token);
    	
        // 남은 유효시간
        Date expiration = extractAllClaims(token).getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        Long expirationAsLong = expiration.getTime() - now;
        return expirationAsLong.intValue();
    }

    // 토큰 유효성 검사
    public Boolean validateToken(String token) {
    	log.info("JwtTokenUtil > validateToken - 호출 (token : {})", token);
    	
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("JwtTokenUtil > validateToken - 유효하지 않은 JWT 서명입니다.");
            throw new SignatureException(token);
        } catch (MalformedJwtException ex) {
            log.error("JwtTokenUtil > validateToken - 올바르지 않은 JWT 토큰입니다.");
            throw new MalformedJwtException(token);
        } catch (ExpiredJwtException ex) {
            log.error("JwtTokenUtil > validateToken - 만료된 JWT 토큰입니다.");
            throw new NullPointerException(token);
        } catch (UnsupportedJwtException ex) {
            log.error("JwtTokenUtil > validateToken - 지원하지 않는 형식의 JWT 토큰입니다.");
            throw new UnsupportedJwtException(token);
        } catch (IllegalArgumentException ex) {
            log.error("JwtTokenUtil > validateToken - 정보가 담겨있지 않은 빈 토큰입니다.");
            throw new IllegalArgumentException(token);
        }
    }
}
