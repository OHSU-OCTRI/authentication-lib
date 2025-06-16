package org.octri.authentication.server.security.exception;

/**
 * Parent for exceptions thrown when creating or updating users.
 */
public class UserManagementException extends Exception {

	/**
	 * This message is presented to users.
	 */
	private static final String GENERIC_MESSAGE = "An unexpected user management error has occurred. Please contact an administrator.";

	/**
	 * Constructs an exception with the default message.
	 */
	public UserManagementException() {
		super(GENERIC_MESSAGE);
	}

	/**
	 * Constructs an exception with a custom error message.
	 *
	 * @param message
	 *            custom error message
	 */
	public UserManagementException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the default error message and the given cause.
	 *
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public UserManagementException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs an exception with a custom error message and cause.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public UserManagementException(String message, Throwable cause) {
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
	public UserManagementException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
