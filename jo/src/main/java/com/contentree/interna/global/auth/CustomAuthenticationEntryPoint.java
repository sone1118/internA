package com.contentree.interna.global.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.contentree.interna.global.model.BaseResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.error("CustomAuthenticationEntryPoint - 호출 > 로그인하지 않은 유저 접근");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        httpServletResponse.setStatus(401);
//        httpServletResponse.setContentType("application/json;charset=utf-8");
//        BaseResponseBody response = new BaseResponseBody(401 ,"유저 정보가 존재하지 않습니다.");
//
//        PrintWriter out = httpServletResponse.getWriter();
//        String jsonResponse = objectMapper.writeValueAsString(response);
//        out.print(jsonResponse);
        httpServletResponse.sendRedirect("/jo/");
    }
}
