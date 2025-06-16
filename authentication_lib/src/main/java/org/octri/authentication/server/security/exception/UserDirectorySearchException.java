package org.octri.authentication.server.security.exception;

/**
 * An exception thrown when saving an LDAP user that is not found in the directory.
 */
public class UserDirectorySearchException extends UserManagementException {

	/**
	 * This message is presented to users.
	 */
	private static final String NOT_IN_LDAP_MESSAGE = "The username provided could not be found in LDAP.";

	/**
	 * Constructs an exception with the default message.
	 */
	public UserDirectorySearchException() {
		super(NOT_IN_LDAP_MESSAGE);
	}

	/**
	 * Constructs an exception with a custom error message.
	 *
	 * @param message
	 *            custom error message
	 */
	public UserDirectorySearchException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the default error message and the given cause.
	 *
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public UserDirectorySearchException(Throwable cause) {
		super(NOT_IN_LDAP_MESSAGE, cause);
	}

	/**
	 * Constructs an exception with a custom error message and cause.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public UserDirectorySearchException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with all fields overridden.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 * @param enableSuppression
	 *            whether suppression is enabled
	 * @param writableStackTrace
	 *            whether the stack trace should be writable
	 */
	public UserDirectorySearchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
