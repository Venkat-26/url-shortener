package com.personal.url_shortener.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Map<String, String> body){
		
		String email = body.get("email");
		String password = body.get("password");
		
		if(password == null || email == null) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "email and password are required"));
		}
		
		return ResponseEntity.ok(authService.register(email, password));
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body){
		String email = body.get("email");
		String password = body.get("password");
		
		if(email == null || password == null) {
			return ResponseEntity.badRequest()
					.body(Map.of("error","email and password are required"));
		}
		
		
		return ResponseEntity.ok(authService.login(email, password));
	}

}
