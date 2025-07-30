package com.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import com.bank.config.UserDetailsServiceImpl;
import com.bank.dto.AuthRequest;
import com.bank.dto.ForgotPasswordRequest;
import com.bank.dto.JwtResponse;
import com.bank.dto.LoginRequest;
import com.bank.dto.ResetPasswordRequest;

import com.bank.service.UserLoginService;

@RestController
@RequestMapping("/auth")
public class UserLoginController {

	private final UserLoginService authService;

	public UserLoginController(UserLoginService authService, UserDetailsServiceImpl userDetailsService) {

		this.authService = authService;

	}

	@PostMapping("/signup")
	public ResponseEntity<?> createPassword(@RequestBody AuthRequest request) {
		try {
			String message = authService.createPassword(request.getEmail(), request.getPassword());
			return ResponseEntity.status(HttpStatus.CREATED).body(message);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			String token = authService.login(request.getEmail(), request.getPassword());
			return ResponseEntity.ok(new JwtResponse(token));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
		String response = authService.forgotPassword(request.getEmail());
		if (response.startsWith("Error")) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
		String response = authService.resetPassword(request.getToken(), request.getNewPassword());
		if (response.startsWith("Error")) {
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

}
