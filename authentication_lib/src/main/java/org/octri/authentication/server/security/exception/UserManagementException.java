package org.octri.authentication.server.security.exception;

/**
 * Parent for exceptions thrown when creating or updating users.
 */
public class UserManagementException extends Exception {

	private static final String GENERIC_MESSAGE = "An unexpected user management error has occurred. Please contact an administrator.";

	public UserManagementException() {
		super(GENERIC_MESSAGE);
	}

	public UserManagementException(String message) {
		super(message);
	}

	public UserManagementException(Throwable cause) {
		super(cause);
	}

	public UserManagementException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserManagementException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
