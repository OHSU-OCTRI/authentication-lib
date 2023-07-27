package org.octri.authentication.server.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.service.UserService;
import org.octri.authentication.server.security.service.UserUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Custom {@link UserDetailsService} implementation that loads user details from the database via our
 * {@link UserService} and {@link UserUserRoleService}.
 *
 * @author harrelst
 *
 */
@Service
public class AuthenticationUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserUserRoleService userUserRoleService;

	/**
	 *
	 * @return - the current user, or null if there is no current authenticated user.
	 */
	public AuthenticationUserDetails getCurrent() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (auth == null) ? null : (AuthenticationUserDetails) auth.getPrincipal();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("Invalid username " + username);
		}

		return new AuthenticationUserDetails(user, getAuthorities(user));
	}

	/**
	 * Determines the user's security authorities. May be overridden.
	 *
	 * @param user
	 *            the user for whom to calculate authorities
	 * @return the list of authorities
	 */
	protected Collection<GrantedAuthority> getAuthorities(User user) {
		List<UserRole> roles = userUserRoleService.findUserRolesByUser(user);
		List<GrantedAuthority> list = roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName()))
				.collect(Collectors.toList());
		return list;
	}

	/**
	 * Accesses the HttpServletRequest and wraps the ipAddress checks to test for a proxy
	 *
	 * @return ipAddress
	 */
	public String getRemoteAddress() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		return RequestUtils.getClientIpAddr(request);
	}
}
