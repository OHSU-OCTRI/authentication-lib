package org.octri.authentication.server.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom Spring Security {@link UserDetails} implementation.
 *
 * @author yateam
 *
 */
public class AuthenticationUserDetails extends User {

	private static final long serialVersionUID = 1L;

	private long userId;

	public AuthenticationUserDetails(org.octri.authentication.server.security.entity.User user,
			Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), (user.getPassword() == null ? "Invalid password" : user.getPassword()),
				user.isEnabled(), !user.getAccountExpired(), !user.getCredentialsExpired(), !user.getAccountLocked(),
				authorities);
		this.userId = user.getId();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
