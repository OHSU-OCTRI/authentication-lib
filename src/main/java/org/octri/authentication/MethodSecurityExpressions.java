package org.octri.authentication;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Expressions for use in the Spring Security {@link PreAuthorize},
 * {@link PostAuthorize}, or {@link PostFilter} method security annotations.
 */
public class MethodSecurityExpressions {

	/**
	 * Requires USER role.
	 */
	public static final String USER = "hasRole('USER')";

	/**
	 * Requires ADMIN role.
	 */
	public static final String ADMIN = "hasRole('ADMIN')";

	/**
	 * Requires SUPER role.
	 */
	public static final String SUPER = "hasRole('SUPER')";

	/**
	 * User must not be the authenticated user. Method must pass an 'id' parameter;
	 */
	public static final String NOT_PRINCIPAL = "#id != principal.userId";

	/**
	 * Requires ADMIN or SUPER role.
	 */
	public static final String ADMIN_OR_SUPER = ADMIN + " or " + SUPER;

	/**
	 * Requires ADMIN or SUPER role, and the user must not be the authenticated
	 * user.
	 */
	public static final String EDIT_USER = "(" + ADMIN_OR_SUPER + ") and " + NOT_PRINCIPAL;

}
