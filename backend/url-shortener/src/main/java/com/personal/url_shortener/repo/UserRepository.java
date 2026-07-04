package com.personal.url_shortener.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.url_shortener.entity.CustomUser;

public interface UserRepository extends JpaRepository<CustomUser, String>{
	
	Optional<CustomUser> findByEmailId(String email);

}
