package org.octri.authentication.server.security.password;

import org.passay.PasswordValidator;

/**
 * A utility for getting password validation error strings that are presented when password validation fails.
 */
public class Reason {

	/**
	 * These keys are produced by the {@link PasswordValidator}. See
	 * {@link https://github.com/vt-middleware/passay/blob/0f107af039e1bf4e617a65287f7c3d6199094ce8/src/main/resources/messages.properties}
	 * for a full list.
	 *
	 * @author sams
	 */
	public enum ReasonKey {
		TOO_SHORT, INSUFFICIENT_DIGIT, INSUFFICIENT_UPPERCASE, INSUFFICIENT_SPECIAL, INSUFFICIENT_CHARACTERISTICS;
	}

	/**
	 * Returns a message to be displayed to the user when a password fails validation.
	 *
	 * @param key
	 * @return The message as a String.
	 */
	public static String message(final String key) {
		switch (ReasonKey.valueOf(key)) {
			case TOO_SHORT:
				return Messages.PASSWORD_TOO_SHORT;
			case INSUFFICIENT_DIGIT:
				return Messages.PASSWORD_INSUFFICIENT_DIGIT;
			case INSUFFICIENT_UPPERCASE:
				return Messages.PASSWORD_INSUFFICIENT_UPPERCASE;
			case INSUFFICIENT_SPECIAL:
				return Messages.PASSWORD_INSUFFICIENT_SPECIAL;
			case INSUFFICIENT_CHARACTERISTICS:
				return Messages.PASSWORD_INSUFFICIENT_CHARACTERISTICS;
			default:
				return Messages.DEFAULT_ERROR_MESSAGE;
		}
	}

	private Reason() {
	}
}