package com.kiran.securesockets.common.authentication;

import com.kiran.securesockets.common.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class CustomUserDetailsService implements UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Autowired
	private UserMasterRepository userMasterRepository;
	
	@Override
	@Transactional
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userMasterRepository.findValidUser(username);
		if (null == user) {
			LOGGER.error("User not found with Username : {}", username);
			throw new UsernameNotFoundException("We don't recognize that Username. Please try again.");
		}
		return CustomUserDetails.create(user, "USER");
	}
	
	@Transactional
	public UserDetails loadUserById(Long userId) {
		User user = userMasterRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User => " + userId));
		return CustomUserDetails.create(user, "USER");
	}
}
