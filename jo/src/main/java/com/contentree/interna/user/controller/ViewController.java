package com.contentree.interna.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
	@GetMapping("/login")
	public String scialSuccess(Model model, @RequestParam(value = "provider", required = false) String provider,
			@RequestParam(value = "oauthId", required = false) String oauthId) {

		return "socail-success";
	}
}