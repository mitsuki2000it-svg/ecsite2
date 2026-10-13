package com.example.ecsite2.controller;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthController {
	
	@GetMapping("/is-admin")
	public boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole("ADMIN");
	}
	@GetMapping("/current-user")
	public String currentUser(Authentication authentication) {
		return authentication.getName();
	}

}
