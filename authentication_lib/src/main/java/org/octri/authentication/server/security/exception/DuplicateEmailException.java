package org.octri.authentication.server.security.exception;

/**
 * An exception thrown when trying to save a user with an email address that belongs to another user.
 *
 * @author yateam
 */
public class DuplicateEmailException extends UserManagementException {

	private static final long serialVersionUID = 1L;

	/**
	 * This message is presented to users.
	 */
	public static final String DUPLICATE_EMAIL_MESSAGE = "The provided email is already in use.";

	/**
	 * Default constructor. Uses {@link #DUPLICATE_EMAIL_MESSAGE} as the error message.
	 */
	public DuplicateEmailException() {
		super(DUPLICATE_EMAIL_MESSAGE);
	}

	/**
	 * Constructs an exception with a custom error message.
	 * 
	 * @param message
	 *            custom error message
	 */
	public DuplicateEmailException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the default {@link #DUPLICATE_EMAIL_MESSAGE} error message and the given cause.
	 * 
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public DuplicateEmailException(Throwable cause) {
		super(DUPLICATE_EMAIL_MESSAGE, cause);
	}

	/**
	 * Constructs an exception with a custom error message and cause.
	 * 
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public DuplicateEmailException(String message, Throwable cause) {
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
	public DuplicateEmailException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
