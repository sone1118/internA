package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WithdrawalController {

	// [ 손헤진 ] go to withdrawal.html
    @GetMapping("/withdrawal")
    public ModelAndView home(@RequestParam(value = "error", required = false) String error) {

    	ModelAndView model = new ModelAndView();
    	model.setViewName("withdrawal");
    	
    	return model;
    }
}
