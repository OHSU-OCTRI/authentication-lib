package org.octri.authentication.server.security.password;

/**
 * A class for holding messages that can be presented to the user.
 */
public class Messages {

	public static final String TITLE_CHANGE_PASSWORD = "Change Password";

	public static final String TITLE_RESET_PASSWORD = "Reset Password";

	public static final String INVALID_PASSWORD_RESET_TOKEN = "Could not validate password token. If this continues to happen please contact your administrator for assistance.";

	public static final String PASSWORD_INVALID = "The password does not meet all of the requirements.";

	public static final String PASSWORD_TOO_SHORT = "Passwords must be at least 8 characters.";

	public static final String PASSWORD_INSUFFICIENT_DIGIT = "Passwords must contain a number.";

	public static final String PASSWORD_INSUFFICIENT_UPPERCASE = "Passwords must contain a capital letter.";

	public static final String PASSWORD_INSUFFICIENT_SPECIAL = "Passwords must contain a special character: space ! \" # $ % & ' ( ) * + , - . / : ; < = > ? @ \\ ^ _ ` { | } ~";

	/**
	 * Note: INSUFFICIENT_CHARACTERISTICS is returned when either INSUFFICIENT_UPPERCASE or INSUFFICIENT_SPECIAL is
	 * thrown.
	 */
	public static final String PASSWORD_INSUFFICIENT_CHARACTERISTICS = "Passwords must contain an additional capital letter OR a special character: space ! \" # $ % & ' ( ) * + , - . / : ; < = > ? @ \\ ^ _ ` { | } ~";

	public static final String MUST_NOT_USE_CURRENT_PASSWORD = "Must not use current password";

	public static final String PASSWORDS_MUST_NOT_INCLUDE_USERNAME = "Passwords must not include username";

	public static final String NEW_AND_CONFIRM_PASSWORDS_MISMATCH = "The value of 'New Password' does not match 'Confirm New Password'.";

	public static final String CURRENT_PASSWORD_INCORRECT = "Current password doesn't match existing password.";

	public static final String COULD_NOT_FIND_AN_EXISTING_USER = "Could not find an existing user.";

	public static final String COULD_NOT_FIND_USERNAME_IN_SESSION = "Could not find username in session.";

	public static final String DEFAULT_ERROR_MESSAGE = "An error occurred. If this continues please contact your administrator.";

	private Messages() {
	}
}