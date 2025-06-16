package org.octri.authentication.server.security.exception;

/**
 * An exception primarily for when adding new users. If the provided user details do not match those of LDAP this
 * exception may be thrown.
 *
 * @author sams
 */
public class InvalidLdapUserDetailsException extends UserManagementException {

	private static final long serialVersionUID = 1L;

	/**
	 * This message is presented to users.
	 */
	public static final String INVALID_USER_DETAILS_MESSAGE = "The provided email does not match the one in LDAP. You may use the 'LDAP Lookup' button to populate the user details.";

	/**
	 * Default constructor. Uses {@link #INVALID_USER_DETAILS_MESSAGE} as the error message.
	 */
	public InvalidLdapUserDetailsException() {
		super(INVALID_USER_DETAILS_MESSAGE);
	}

	/**
	 * Constructs an exception with a custom error message.
	 *
	 * @param message
	 *            custom error message
	 */
	public InvalidLdapUserDetailsException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the default {@link #INVALID_USER_DETAILS_MESSAGE} error message and the given cause.
	 *
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public InvalidLdapUserDetailsException(Throwable cause) {
		super(INVALID_USER_DETAILS_MESSAGE, cause);
	}

	/**
	 * Constructs an exception with a custom error message and cause.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public InvalidLdapUserDetailsException(String message, Throwable cause) {
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
	public InvalidLdapUserDetailsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
