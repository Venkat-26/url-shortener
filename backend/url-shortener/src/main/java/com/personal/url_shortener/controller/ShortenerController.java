package com.personal.url_shortener.controller;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.personal.url_shortener.entity.CustomUrl;
import com.personal.url_shortener.entity.CustomUser;
import com.personal.url_shortener.repo.UserRepository;
import com.personal.url_shortener.service.ClickEventService;
import com.personal.url_shortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/api/")
@RequiredArgsConstructor
public class ShortenerController {
	
	private final UserRepository userRepository;
	private final UrlService urlService;
	private final StringRedisTemplate stringRedisTemplate;
	private final ClickEventService clickEventService;


	@PostMapping("/shorten")
	public ResponseEntity<?> getShortenUrl(@RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
		
		String longUrl = body.get("longUrl");
		
		if(longUrl == null || longUrl.isBlank()) {
			
			return ResponseEntity.badRequest().body(Map.of("error","Long Url is required."));
		}
		
//		CustomUser user = userRepository.findByEmailId(userDetails.getUsername())
//				.orElseGet(() -> userRepository.save(CustomUser.builder()
//						.emailId("testexample@gmail.com")
//						.password("temporary")
//						.build()));
		
//		UserDetails userDetails = userDetailsService.loadUserByUsername(longUrl)
		CustomUser user = userRepository.findByEmailId(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		CustomUrl saved = urlService.shortenUrl(longUrl, user);
		
		
		return ResponseEntity.ok(Map.of(
				"shortCode", saved.getShortCode(),
				"shortUrl", "http://localhost:8080/" + saved.getShortCode(),
				"longUrl", saved.getLongUrl(),
				"createdAt",saved.getCreatedAt().toString()));
	}
	
	@GetMapping("/{shortCode}")
	public RedirectView redirect(@PathVariable String shortCode, HttpServletRequest request) {
		
		CustomUrl url = urlService.incrementClickAndGet(shortCode);
		
		clickEventService.save(shortCode, request);
		return new RedirectView(url.getLongUrl());
	}
	
	@GetMapping("/info/{shortCode}")
	public ResponseEntity<?> info(@PathVariable String shortCode){
			CustomUrl url = urlService.getByShortCode(shortCode);
			
			return ResponseEntity.ok(Map.of(
					"shortCode", url.getShortCode(),
					"longUrl", url.getLongUrl(),
					"clickCount", url.getClickCount(),
					"createdAt", url.getCreatedAt().toString()));
	}
	

	@GetMapping("/test-redis")
	public ResponseEntity<?> testRedis() {
	    System.out.println(">>> testRedis endpoint hit");
	    System.out.println(">>> stringRedisTemplate = " + stringRedisTemplate);

	    if (stringRedisTemplate == null) {
	        return ResponseEntity.status(500)
	                .body(Map.of("error", "StringRedisTemplate is NULL"));
	    }

	    try {
	        stringRedisTemplate.opsForValue().set("test-key", "hello-redis");
	        String value = stringRedisTemplate.opsForValue().get("test-key");
	        return ResponseEntity.ok(Map.of("redis-value", value, "status", "connected"));
	    } catch (Exception e) {
	        return ResponseEntity.status(500)
	                .body(Map.of("error", e.getMessage()));
	    }
	}

}
