package com.personal.url_shortener.service;

import org.springframework.stereotype.Service;

import com.personal.url_shortener.entity.ClickEvent;
import com.personal.url_shortener.repo.ClickEventRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClickEventService {
	
	private final ClickEventRepository clickEventRepository;
	
	public void save(String shortCode, HttpServletRequest request) {
		
		String userAgent = request.getHeader("User-Agent");
		
		ClickEvent event = ClickEvent.builder()
				.shortCode(shortCode)
				.ipAddress(getClientIp(request))
				.device(detectDevice(userAgent))
				.browser(detectBrowser(userAgent))
				.referrer(request.getHeader("Referer"))
				.build();
		
		clickEventRepository.save(event);
	}
	
	
	private String getClientIp(HttpServletRequest request) {
		
		String ip = request.getHeader("X-Forward-For");
		return (ip !=null) ? ip.split(",")[0] : request.getRemoteAddr();
	}
	
	private String detectDevice(String userAgent) {
		
		if(userAgent == null) return "unknown";
		if(userAgent.toLowerCase().contains("mobile")) return "mobile";
		if(userAgent.toLowerCase().contains("tablet")) return "tablet";
		return "desktop";
	}
	
	
	private String detectBrowser(String userAgent) {
		
		if(userAgent == null) return "unknown";
		if(userAgent.contains("Chrome")) return "Chrome";
		if(userAgent.contains("Firefox")) return "Firefox";
		if(userAgent.contains("Safari")) return "Safari";
		if(userAgent.contains("Edge")) return "Edge";
		
		return "Other";
		
	}

}
