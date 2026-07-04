package com.personal.url_shortener.util;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.personal.url_shortener.entity.CustomUser;
import com.personal.url_shortener.repo.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepository;
	
	@Cacheable(value="users",key="#email")
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println(">>> Loading user from DB:" + email);
		CustomUser user = userRepository.findByEmailId(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found "+ email));
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmailId())
				.password(user.getPassword())
				.roles("USER")
				.build();
	}
	
	
	

}
