package org.octri.authentication.server.security.exception;

/**
 * An exception thrown when trying to save a user with an email address that belongs to another user.
 *
 * @author yateam
 */
public class DuplicateEmailException extends UserManagementException {

	private static final long serialVersionUID = 1L;

	// This message is presented to users.
	public static final String DUPLICATE_EMAIL_MESSAGE = "The provided email is already in use.";

	public DuplicateEmailException() {
		super(DUPLICATE_EMAIL_MESSAGE);
	}

	public DuplicateEmailException(String message) {
		super(message);
	}

	public DuplicateEmailException(Throwable cause) {
		super(DUPLICATE_EMAIL_MESSAGE, cause);
	}

	public DuplicateEmailException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateEmailException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
