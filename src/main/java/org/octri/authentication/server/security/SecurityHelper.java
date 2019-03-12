package org.octri.authentication.server.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

/**
 * A class for querying Spring Security Authentication. This was added after porting the ThymeLeaf templates to
 * Mustache. ThymeLeaf templates used a tag library for interacting with Spring Security, but with Mustache we need to
 * handle this in the Controller.<br>
 * <br>
 * With this class you may check user roles, determine if a user is logged in, or anonymous, and get the authenticated
 * username.<br>
 * <br>
 * All available roles for the system are kept in the {@link SecurityHelper.Role} enum.
 */
public class SecurityHelper {

	public static enum Role {
		ROLE_USER, ROLE_ADMIN, ROLE_SUPER;
	}

	private Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

	private Authentication authentication;

	public SecurityHelper(SecurityContext context) {
		authentication = context.getAuthentication();
		if (authentication != null) {
			authorities = authentication.getAuthorities();
		}
	}

	/**
	 * Checks if the user is authenticated and not anonymous.
	 *
	 * @return
	 */
	public boolean isLoggedIn() {
		return isAuthenticated() && !isAnonymous();
	}

	/**
	 * Checks if the user is authenticated. The anonymous user is authenticated by default. Use {@link #isLoggedIn()} if
	 * you want to ensure the user is authenticated but not anonymous.
	 *
	 * @return
	 */
	public boolean isAuthenticated() {
		return authentication == null ? false : authentication.isAuthenticated();
	}

	/**
	 * Checks if the user is an anonymously authenticated user.
	 *
	 * @return
	 */
	public boolean isAnonymous() {
		return authentication == null ? true : authentication instanceof AnonymousAuthenticationToken;
	}

	/**
	 * Checks if the currently authenticated user is allowed to edit a specific user. Currently a user must have the
	 * ADMIN or SUPER role, and must not be editing themself.
	 * 
	 * @param testUserId
	 *            The id of the user being edited.
	 * @return True if the currently authenticated user can edit a user.
	 */
	public boolean canEditUser(Long testUserId) {
		return isAdminOrSuper() && authenticationUserDetails().getUserId() != testUserId;
	}

	/**
	 * Get the authenticated username.
	 *
	 * @return
	 */
	public String username() {
		return authentication == null ? "" : authentication.getName();
	}

	/**
	 * Get the current {@link AuthenticationUserDetails}.
	 * 
	 * @return
	 */
	public AuthenticationUserDetails authenticationUserDetails() {
		return authentication == null ? null : (AuthenticationUserDetails) authentication.getPrincipal();
	}

	/**
	 * Check if the user has at least one of the roles: ADMIN or SUPER.
	 *
	 * @return
	 */
	public boolean isAdminOrSuper() {
		return hasAnyRole(Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_SUPER));
	}

	/**
	 * Checks if user contains the given role.
	 *
	 * @param role
	 *            A user role.
	 * @return True if the user contains the role.
	 */
	public boolean hasRole(Role role) {
		return hasRoleName(role.name());
	}
	
	/**
	 * Checks if the user has the given roleName. This could be any role defined by the application, not
	 * just the default roles set up in this library.
	 * 
	 * @param roleName
	 * @return True if the user contains the roleName
	 */
	public boolean hasRoleName(String roleName) {
		return authorities == null ? false
				: authorities.stream().anyMatch(authority -> authority.getAuthority().equals(roleName));		
	}

	/**
	 * Checks if a user contains at least one of the roles.
	 *
	 * @param roles
	 *            A list of user roles.
	 * @return True if the user contains one of the roles.
	 */
	public boolean hasAnyRole(List<Role> roles) {
		return hasAnyRoleName(roles.stream().map(role -> role.name()).collect(Collectors.toList()));
	}
	
	/**
	 * Checks if a user contains at least one of the role names.
	 * 
	 * @param roleNames
	 * @return True if the user contains at least one of the role names.
	 */
	public boolean hasAnyRoleName(List<String> roleNames) {
		return roleNames.stream().anyMatch(name -> hasRoleName(name));
	}
}
