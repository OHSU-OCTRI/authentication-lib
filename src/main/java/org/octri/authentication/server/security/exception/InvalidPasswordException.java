package org.octri.authentication.server.security.exception;

/**
 * An exception for marking invalid passwords.
 * 
 * @author sams
 */
public class InvalidPasswordException extends Exception {

	private static final long serialVersionUID = -2066695845899054899L;

	public InvalidPasswordException() {
	}

	public InvalidPasswordException(String message) {
		super(message);
	}

	public InvalidPasswordException(Throwable cause) {
		super(cause);
	}

	public InvalidPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPasswordException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
