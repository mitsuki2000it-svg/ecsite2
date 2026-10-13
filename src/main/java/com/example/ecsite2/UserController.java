package com.example.ecsite2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto dto) {

		try {
			userService.register(dto);

			return ResponseEntity.ok("登録成功");
		} catch (IllegalArgumentException e) {

			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
}
