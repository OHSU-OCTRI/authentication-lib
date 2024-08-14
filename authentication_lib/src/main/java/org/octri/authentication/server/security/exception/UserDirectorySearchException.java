package org.octri.authentication.server.security.exception;

/**
 * An exception thrown when saving an LDAP user that is not found in the directory.
 */
public class UserDirectorySearchException extends UserManagementException {

	private static final String NOT_IN_LDAP_MESSAGE = "The username provided could not be found in LDAP.";

	public UserDirectorySearchException() {
		super(NOT_IN_LDAP_MESSAGE);
	}

	public UserDirectorySearchException(String message) {
		super(message);
	}

	public UserDirectorySearchException(Throwable cause) {
		super(NOT_IN_LDAP_MESSAGE, cause);
	}

	public UserDirectorySearchException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserDirectorySearchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
