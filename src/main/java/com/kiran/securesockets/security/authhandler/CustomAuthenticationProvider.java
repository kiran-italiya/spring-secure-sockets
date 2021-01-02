package com.kiran.securesockets.security.authhandler;

import com.kiran.securesockets.common.authentication.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	private volatile String userNotFoundEncodedPassword;
	private PasswordEncoder passwordEncoder;
	private UserDetailsService userDetailsService;
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!authentication.getClass().equals(UsernamePasswordAuthenticationToken.class)) {
			throw new IllegalArgumentException("Only Username Password Authentication Token is supported.");
		}
		String username = (null == authentication.getPrincipal()) ? "NO_USERNAME_PROVIDED" : authentication.getName();
		CustomUserDetails user = null;
		try {
			user = retrieveUser(username, (UsernamePasswordAuthenticationToken) authentication);
		} catch (UsernameNotFoundException notFound) {
			throw notFound;
		}
		try {
			preAuthenticationCheck(user);
			additionalAuthenticationChecks(user,
					(UsernamePasswordAuthenticationToken) authentication);
		} catch (AuthenticationException ex) {
			throw ex;
		}
		postAuthenticationCheck(user);
		return createSuccessAuthentication(user, authentication, user);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected final CustomUserDetails retrieveUser(String username,
	                                               UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		prepareTimingAttackProtection();
		try {
			CustomUserDetails loadedUser = (CustomUserDetails) this.getUserDetailsService().loadUserByUsername(username);
			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		} catch (UsernameNotFoundException ex) {
			mitigateAgainstTimingAttack(authentication);
			throw ex;
		} catch (InternalAuthenticationServiceException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}

	protected Authentication createSuccessAuthentication(Object principal,
	                                                     Authentication authentication, UserDetails user) {
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				principal, authentication.getCredentials(),
				authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());
		return result;
	}

	private void prepareTimingAttackProtection() {
		if (this.userNotFoundEncodedPassword == null) {
			this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
		}
	}

	private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
		if (authentication.getCredentials() != null) {
			String presentedPassword = authentication.getCredentials().toString();
			this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
		}
	}


	private void preAuthenticationCheck(CustomUserDetails user) {
		if (!user.isAccountNonLocked()) {
			logger.debug("User account is locked");

			throw new LockedException("Your account is locked. Please contact to administrator.");
		}

		if (!user.isEnabled()) {
			logger.debug("User account is disabled");

			throw new DisabledException("Your account is disabled. Please contact to administrator.");
		}

		if (!user.isAccountNonExpired()) {
			logger.debug("User account is expired");

			throw new AccountExpiredException("Your account has expired.");
		}
	}

	protected void additionalAuthenticationChecks(UserDetails userDetails,
	                                              UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException("Incorrect username or password.");
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException("Incorrect username or password.");
		}
	}

	private void postAuthenticationCheck(CustomUserDetails user) {
		if (!user.isCredentialsNonExpired()) {
			logger.debug("User account credentials have expired");
			throw new CredentialsExpiredException("User credentials have expired");
		}
	}
}