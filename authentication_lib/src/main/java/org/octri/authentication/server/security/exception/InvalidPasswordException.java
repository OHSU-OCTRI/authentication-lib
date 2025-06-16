package org.octri.authentication.server.security.exception;

/**
 * An exception for marking invalid passwords.
 *
 * @author sams
 */
public class InvalidPasswordException extends Exception {

	private static final long serialVersionUID = -2066695845899054899L;

	/**
	 * Default constructor.
	 */
	public InvalidPasswordException() {
	}

	/**
	 * Constructs an exception with a custom error message.
	 *
	 * @param message
	 *            custom error message
	 */
	public InvalidPasswordException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the default (empty) error message and the given cause.
	 *
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public InvalidPasswordException(Throwable cause) {
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
	public InvalidPasswordException(String message, Throwable cause) {
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
	public InvalidPasswordException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
