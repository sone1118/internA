package com.contentree.interna.user.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 김지슬
 *
 */
@Controller
@Slf4j
public class CustomErrorController implements ErrorController{
    
    @ExceptionHandler(Throwable.class)
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String statusMsg = status.toString();
        model.addAttribute("code", statusMsg);
        
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        if (message != null) {
        	model.addAttribute("msg", message.toString());
        	log.info("CustomErrorController > handleError - 호출 (코드 : {}, 에러 메시지: {})", statusMsg, message.toString());
        } else {
        	HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(statusMsg));
        	model.addAttribute("msg", httpStatus);
        	log.info("CustomErrorController > handleError - 호출 (코드 : {}, 에러 메시지: {})", statusMsg, httpStatus);
        }

        return "error";
    }
}
