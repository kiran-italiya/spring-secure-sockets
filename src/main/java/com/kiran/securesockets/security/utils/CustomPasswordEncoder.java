package com.kiran.securesockets.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder {
	private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	
	private CustomPasswordEncoder() {
		throw new IllegalStateException("Implementation is not allowed for this class.");
	}
	
	public static String encode(String password) {
		return PASSWORD_ENCODER.encode(password);
	}
}
