package com.contentree.interna.user.entity;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface UserDetails extends Serializable {
	Collection<? extends GrantedAuthority> getAuthorities();

	boolean isAccountNonExpired();

	boolean isAccountNonLocked();

	boolean isCredentialsNonExpired();

	boolean isEnabled();
	// String getPassword();
	// String getUsername();
}
