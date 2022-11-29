package com.contentree.interna.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.contentree.interna.global.auth.CustomAccessDeniedHandler;
import com.contentree.interna.global.auth.CustomAuthenticationEntryPoint;
import com.contentree.interna.global.auth.JwtAuthenticationFilter;
import com.contentree.interna.global.auth.JwtExceptionFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; 
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtExceptionFilter jwtExceptionFilter;
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/v2/api-docs", "/swagger*/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()	// csrf 미적용
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 사용 하지않음
			.and()
			.httpBasic().authenticationEntryPoint(customAuthenticationEntryPoint)   // 인증 되지 않은 유저가 요청했을때 동작
			.and()
			.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler) // 액세스 할 수 없는 요청 했을 시 동작
			.and()
			.authorizeRequests()
			.antMatchers("/api/users/login").permitAll()	// api 권한 설정 
			.anyRequest().authenticated();
		
		// jwt filter 적용 
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		// jwt 예외 프론트로 반환하기 위한 filter 적용 
        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
		return http.build();
	}
}
