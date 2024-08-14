package org.octri.authentication.server.security.exception;

/**
 * An exception primarily for when adding new users. If the provided user details do not match those of LDAP this
 * exception may be thrown.
 *
 * @author sams
 */
public class InvalidLdapUserDetailsException extends UserManagementException {

	private static final long serialVersionUID = 1L;

	// This message is presented to users.
	public static final String INVALID_USER_DETAILS_MESSAGE = "The provided email does not match the one in LDAP. You may use the 'LDAP Lookup' button to populate the user details.";

	public InvalidLdapUserDetailsException() {
		super(INVALID_USER_DETAILS_MESSAGE);
	}

	public InvalidLdapUserDetailsException(String message) {
		super(message);
	}

	public InvalidLdapUserDetailsException(Throwable cause) {
		super(INVALID_USER_DETAILS_MESSAGE, cause);
	}

	public InvalidLdapUserDetailsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLdapUserDetailsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
