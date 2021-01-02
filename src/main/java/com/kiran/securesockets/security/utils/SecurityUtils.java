package com.kiran.securesockets.security.utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

@Component
public class SecurityUtils {

	private static final ResponseStatusException UNAUTHORIZED_EXCEPTION = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is expired.");

	public boolean isAnonymous() {
		Authentication auth = getAuthenticatedPrincipal();
		final String role = auth.getAuthorities().stream().findFirst().get().toString();
		return "ROLE_ANONYMOUS".equalsIgnoreCase(role);
	}

	public Authentication getAuthenticatedPrincipal() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null == auth || !auth.isAuthenticated()) {
			return null;
		}
		return auth;
	}

	public String getUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null == auth || !auth.isAuthenticated()) {
			return null;
		}
		return auth.getName();
	}

	public long getUserId(HttpSession session) {
		if (session == null) {
			throw UNAUTHORIZED_EXCEPTION;
		} else {
			return Long.parseLong(session.getAttribute("auth_user_id").toString());
		}
	}

	public String getUserDisplayName(HttpSession session) {
		if (session == null) {
			throw UNAUTHORIZED_EXCEPTION;
		} else {
			return session.getAttribute("auth_display_name").toString();
		}
	}

	public String getUserType(HttpSession session) {
		if (null == session) {
			throw UNAUTHORIZED_EXCEPTION;
		} else {
			return session.getAttribute("auth_user_type").toString();
		}
	}
}
