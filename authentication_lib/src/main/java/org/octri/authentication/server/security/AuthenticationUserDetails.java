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

	/**
	 * The user's unique ID
	 */
	private long userId;

	/**
	 * Constructor
	 * 
	 * @param user
	 *            a user entity
	 * @param authorities
	 *            user authorities (e.g. roles)
	 */
	public AuthenticationUserDetails(org.octri.authentication.server.security.entity.User user,
			Collection<? extends GrantedAuthority> authorities) {
		super(user.getUsername(), (user.getPassword() == null ? "Invalid password" : user.getPassword()),
				user.isEnabled(), !user.getAccountExpired(), !user.getCredentialsExpired(), !user.getAccountLocked(),
				authorities);
		this.userId = user.getId();
	}

	/**
	 * Gets the user's unique ID
	 * 
	 * @return the user's ID value
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Sets the user's unique ID
	 * 
	 * @param userId
	 *            the user's ID value
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

}
