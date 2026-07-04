package com.personal.url_shortener.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personal.url_shortener.entity.ClickEvent;


@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, String>{
	
	List<ClickEvent> findByShortCode(String shortCode);
	
	List<ClickEvent> findByShortCodeAndClickedAtBetween(
			String shortCode,
			LocalDateTime start,
			LocalDateTime end);
	
	
	long countByShortCode(String shortCode);
	
	
	
}
