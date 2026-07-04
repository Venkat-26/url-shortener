package com.personal.url_shortener.service;

import java.security.SecureRandom;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.personal.url_shortener.entity.CustomUrl;
import com.personal.url_shortener.entity.CustomUser;
import com.personal.url_shortener.repo.UrlRepository;

@Service
public class UrlService {
	
	private final UrlRepository urlRepository;
	
	
	
	public UrlService(UrlRepository urlRepository) {
		super();
		this.urlRepository = urlRepository;
	}


	public static final int SHORT_CODE_LENGTH = 6;
	public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYWZ1234567890";
	private final SecureRandom random = new SecureRandom();
	
	
	
	public CustomUrl shortenUrl(String longUrl, CustomUser user) {
		String shortCode = generateUniqueShortCode();
		
		CustomUrl url = CustomUrl.builder()
				.clickCount(0)
				.longUrl(longUrl)
				.shortCode(shortCode)
				.user(user)
				.build();
		
		
		return urlRepository.save(url);
	}
	
	public CustomUrl incrementClickAndGet(String shortCode) {
		CustomUrl url = getByShortCode(shortCode);
		url.setClickCount(url.getClickCount()+1);
		
		return urlRepository.save(url);
	}
	
	@Cacheable(value = "urls", key = "#shortCode")
	public CustomUrl getByShortCode(String shortCode) {
		System.out.println(">>> CACHE MISS - fetching from DB: " + shortCode);
		return urlRepository.findByShortCode(shortCode)
				.orElseThrow(() -> new RuntimeException("Short Url not found" + shortCode));
	}
	
	
	@CachePut(value = "urls", key = "#shortCode")
	public String generateUniqueShortCode() {
		String code;
		do {
			code = generateUrlShortCode();
		}while(urlRepository.existsByShortCode(code));
		
		return code;
	}
	
	
	public String generateUrlShortCode() {
		StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
		
		for(int i=0;i<SHORT_CODE_LENGTH;i++) {
			sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		
		return sb.toString();
		
	}
	
	@CacheEvict(value = "urls", key="#shortCode")
	public void deleteUrl(String shortCode) {
		urlRepository.findByShortCode(shortCode)
		.ifPresent(urlRepository::delete
				);
	}

}
