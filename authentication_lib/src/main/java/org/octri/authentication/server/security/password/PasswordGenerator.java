package org.octri.authentication.server.security.password;

import org.passay.data.EnglishCharacterData;
import org.passay.rule.CharacterRule;

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
		var lowerCaseLetter = new CharacterRule(EnglishCharacterData.LowerCase, 1);

		// Require at least one capital letter
		var capitalLetter = new CharacterRule(EnglishCharacterData.UpperCase, 1);

		// Require at least one special character
		var special = new CharacterRule(EnglishCharacterData.Special, 1);

		// Require at least one digit
		var digit = new CharacterRule(EnglishCharacterData.Digit, 1);

		var generator = new org.passay.generate.PasswordGenerator(32, lowerCaseLetter, capitalLetter, special, digit);

		return generator.generate().toString();
	}

}
