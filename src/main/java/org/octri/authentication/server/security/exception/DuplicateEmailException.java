package org.octri.authentication.server.security.exception;

/**
 * An exception thrown when trying to save a user with an email address that belongs to another user.
 * 
 * @author yateam
 */
public class DuplicateEmailException extends Exception {

	private static final long serialVersionUID = 1L;

	// This message is presented to users.
	public static final String DUPLICATE_EMAIL_MESSAGE = "The provided email is already in use.";

	public DuplicateEmailException() {
		super(DUPLICATE_EMAIL_MESSAGE);
	}

}
