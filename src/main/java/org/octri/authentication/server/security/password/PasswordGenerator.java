package org.octri.authentication.server.security.password;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

/**
 * Class for generating passwords meeting OHSU standards.
 * 
 * @author sams
 */
public final class PasswordGenerator {

	private PasswordGenerator() {
	}

	/**
	 * Generates a strong password meeting OHSU standards.
	 * 
	 * @return A password.
	 */
	public static String generate() {
		// Require at least one lower case letter
		CharacterRule lowerCaseLetter = new CharacterRule(EnglishCharacterData.LowerCase, 1);

		// Require at least one capital letter
		CharacterRule capitalLetter = new CharacterRule(EnglishCharacterData.UpperCase, 1);

		// Require at least one special character
		CharacterRule special = new CharacterRule(EnglishCharacterData.Special, 1);

		// Require at least one digit
		CharacterRule digit = new CharacterRule(EnglishCharacterData.Digit, 1);

		org.passay.PasswordGenerator generator = new org.passay.PasswordGenerator();

		return generator.generatePassword(32, lowerCaseLetter, capitalLetter, special, digit);
	}

}
