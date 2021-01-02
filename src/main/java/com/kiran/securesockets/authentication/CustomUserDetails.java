package com.kiran.securesockets.authentication;

import com.kiran.securesockets.common.entity.User;
import com.kiran.securesockets.security.utils.CustomPasswordEncoder;
import com.kiran.securesockets.security.utils.PasswordUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
	private static final long serialVersionUID = -6226461305706934160L;
	private User user;
	private Collection<? extends GrantedAuthority> authorities;
	
	public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}
	
	public static CustomUserDetails create(User usermaster, String roleName) {
		return new CustomUserDetails(usermaster, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName)));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		final String decryptedPassword = PasswordUtil.decrypt(user.getPassword(), user.getSecretKey(), user.getCustomSalt());
		return CustomPasswordEncoder.encode(decryptedPassword);
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		if(user.getActive()==1)
			return true;
		else 
			return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CustomUserDetails)) return false;

		CustomUserDetails that = (CustomUserDetails) o;

		if (user != null ? !user.equals(that.user) : that.user != null) return false;
		return authorities != null ? authorities.equals(that.authorities) : that.authorities == null;

	}

	@Override
	public int hashCode() {
		int result = user != null ? user.hashCode() : 0;
		result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
		return result;
	}
}
