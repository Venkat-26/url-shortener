package com.personal.url_shortener.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.url_shortener.entity.CustomUrl;

public interface UrlRepository extends JpaRepository<CustomUrl, String>{
	
	boolean existsByShortCode(String code);
	
	Optional<CustomUrl> findByShortCode(String code);
}
