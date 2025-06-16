package org.octri.authentication.server.security.password;

/**
 * A class for holding messages that can be presented to the user.
 */
public class Messages {

	/**
	 * The header displayed when changing the user's password.
	 */
	public static final String TITLE_CHANGE_PASSWORD = "Change Password";

	/**
	 * The header displayed when resetting the user's password.
	 */
	public static final String TITLE_RESET_PASSWORD = "Reset Password";

	/**
	 * Error message displayed when a user's password may not be reset (e.g. LDAP accounts).
	 */
	public static final String INVALID_PASSWORD_RESET = "Your password cannot be reset. Please contact an administrator for assistance.";

	/**
	 * Error message displayed when a password does not pass complexity validation.
	 */
	public static final String PASSWORD_INVALID = "The password does not meet all of the requirements.";

	/**
	 * Error message displayed when a password is too short.
	 */
	public static final String PASSWORD_TOO_SHORT = "Password too short.";

	/**
	 * Error message displayed when passwords must contain a digit, but a digit is not present.
	 */
	public static final String PASSWORD_INSUFFICIENT_DIGIT = "Missing a number.";

	/**
	 * Error message displayed when passwords must contain a capital letter, but one is not present.
	 */
	public static final String PASSWORD_INSUFFICIENT_UPPERCASE = "Missing a capital letter.";

	/**
	 * Error message displayed when passwords must contain a special character, but one is not present.
	 */
	public static final String PASSWORD_INSUFFICIENT_SPECIAL = "Missing a special character.";

	/**
	 * Note: INSUFFICIENT_CHARACTERISTICS is returned when either INSUFFICIENT_UPPERCASE or INSUFFICIENT_SPECIAL is
	 * thrown.
	 */
	public static final String PASSWORD_INSUFFICIENT_CHARACTERISTICS = "Missing an additional capital letter OR a special character.";

	/**
	 * Error message that is displayed when a user tries to reuse their current password.
	 */
	public static final String MUST_NOT_USE_CURRENT_PASSWORD = "Must not use current password";

	/**
	 * Error message that is displayed when a user tries to use a password containing their username.
	 */
	public static final String PASSWORDS_MUST_NOT_INCLUDE_USERNAME = "Passwords must not include username";

	/**
	 * Error message that is displayed if the new password and password confirmation fields do not match.
	 */
	public static final String NEW_AND_CONFIRM_PASSWORDS_MISMATCH = "New and Confirm passwords don't match.";

	/**
	 * Error message that is displayed when a user is changing their password, and their current password is incorrect.
	 */
	public static final String CURRENT_PASSWORD_INCORRECT = "Current password incorrect.";

	/**
	 * Error message that is displayed if the user cannot be found.
	 */
	public static final String COULD_NOT_FIND_AN_EXISTING_USER = "Could not find an existing user.";

	/**
	 * Error displayed if the username is not present in the session.
	 */
	public static final String COULD_NOT_FIND_USERNAME_IN_SESSION = "Could not find username in session.";

	/**
	 * Generic error message.
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "An error occurred. If this continues please contact your administrator.";

	private Messages() {
	}
}