package com.personal.url_shortener.auth;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.personal.url_shortener.entity.CustomUser;
import com.personal.url_shortener.repo.UserRepository;
import com.personal.url_shortener.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	
	public Map<String, String> register(String email, String password){
		
		if(userRepository.findByEmailId(email).isPresent()) {
			throw new RuntimeException("Email already registered");
		}
		
		
		CustomUser user = CustomUser.builder()
				.emailId(email)
				.password(passwordEncoder.encode(password))
				.build();
		
		
		userRepository.save(user);
		
		String token = jwtUtil.generateToken(email);
		
		return Map.of("token", token, "email",email);
	}
	
	
	public Map<String, String> login(String email, String password){
		CustomUser user = userRepository.findByEmailId(email)
				.orElseThrow(() -> new RuntimeException("Inavlid email or password"));
		
		if(!(passwordEncoder.matches(password, user.getPassword()))) {
			throw new RuntimeException("Invalid email or password");
			
		}
		
		String token = jwtUtil.generateToken(email);
		return Map.of("token", token, "email",email);
	}

}
