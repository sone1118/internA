package com.contentree.interna.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class WithdrawalController {

    @RequestMapping("/withdrawal")
    public ModelAndView home(@RequestParam(value = "error", required = false) String error) {

    	ModelAndView model = new ModelAndView();
    	model.setViewName("withdrawal");
    	
    	return model;
    }
}
