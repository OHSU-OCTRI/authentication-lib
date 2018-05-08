package org.octri.authentication.server.security.exception;

/**
 * An exception primarily for when adding new users. If the provided user details do not match those of LDAP this
 * exception may be thrown.
 * 
 * @author sams
 */
public class InvalidLdapUserDetailsException extends Exception {

	public InvalidLdapUserDetailsException() {
	}

	public InvalidLdapUserDetailsException(String message) {
		super(message);
	}

	public InvalidLdapUserDetailsException(Throwable cause) {
		super(cause);
	}

	public InvalidLdapUserDetailsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLdapUserDetailsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
