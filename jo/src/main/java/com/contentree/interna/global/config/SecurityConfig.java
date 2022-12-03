package com.contentree.interna.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
//	
//	private final CustomAccessDeniedHandler customAccessDeniedHandler;
//	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; 
//	private final JwtAuthenticationFilter jwtAuthenticationFilter;
//	private final JwtExceptionFilter jwtExceptionFilter;
//	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/css/**", "/js/**", "/images/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()	// csrf 미적용
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 사용 하지않음 (security는 기본적으로 세션 사용)
			.and()
			.authorizeRequests()
			.antMatchers("/v2/api-docs", "/swagger*/**").permitAll()
			.antMatchers("/api/users/send-joins").authenticated()	// api 권한 설정 
			.anyRequest().permitAll();
		
		// jwt filter 적용 
//		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//		// jwt 예외 프론트로 반환하기 위한 filter 적용 
//        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
		return http.build();
	}
}
