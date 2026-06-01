package org.octri.authentication.server.security.password;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PasswordConstraintValidatorTest {

	private PasswordConstraintValidator validator;

	@BeforeEach
	public void setUp() {
		validator = new PasswordConstraintValidator();
	}

	@Test
	public void validPasswordWithUppercase() {
		List<String> errors = validator.validate("Passw0rd", null);
		assertTrue(errors.isEmpty(), "Password with uppercase and digit should be valid");
	}

	@Test
	public void validPasswordWithSpecialCharacter() {
		List<String> errors = validator.validate("passw0rd!", null);
		assertTrue(errors.isEmpty(), "Password with special character and digit should be valid");
	}

	@Test
	public void validPasswordMinimumLength() {
		// 7 chars — should fail length
		List<String> errors = validator.validate("Abcde1f", null);
		assertFalse(errors.isEmpty(), "There should be errors if the password is too short");
		assertTrue(errors.contains(Messages.PASSWORD_TOO_SHORT), "The password too short error should be present");
	}

	@Test
	public void validPasswordExactlyEightChars() {
		List<String> errors = validator.validate("Abcde1fg", null);
		assertTrue(errors.isEmpty(), "8-character password meeting all rules should be valid");
	}

	@Test
	public void invalidPasswordNoDigit() {
		List<String> errors = validator.validate("Password!", null);
		assertFalse(errors.isEmpty(), "There should be errors if the password has no digit characters");
		assertTrue(errors.contains(Messages.PASSWORD_INSUFFICIENT_DIGIT),
				"The insufficient digit error should be present");
	}

	@Test
	public void invalidPasswordNoAlphaCharacter() {
		List<String> errors = validator.validate("12345678!", null);
		assertFalse(errors.isEmpty(), "There should be errors if the password has no alphabetical characters");
		assertTrue(errors.contains(Messages.PASSWORD_INVALID), "The general invalid password error should be present");
	}

	@Test
	public void invalidPasswordNoUppercaseOrSpecial() {
		List<String> errors = validator.validate("password1", null);
		assertFalse(errors.isEmpty(), "There should be errors if the password has no uppercase or special characters");
		assertTrue(errors.contains(Messages.PASSWORD_INSUFFICIENT_CHARACTERISTICS),
				"The insufficient characteristics error should be present if there are no uppercase of special characters");
	}

	@Test
	public void onlyInsufficientCharacteristicsIfBothUppercaseAndSpecialMissing() {
		// INSUFFICIENT_UPPERCASE and INSUFFICIENT_SPECIAL should be filtered out;
		// only INSUFFICIENT_CHARACTERISTICS should appear for that rule group.
		List<String> errors = validator.validate("password1", null);
		assertFalse(errors.contains(Messages.PASSWORD_INSUFFICIENT_UPPERCASE),
				"The insufficient uppercase error should not be present");
		assertFalse(errors.contains(Messages.PASSWORD_INSUFFICIENT_SPECIAL),
				"The insufficient special characters error should not be present");
	}

	@Test
	public void invalidPasswordAllRulesFail() {
		List<String> errors = validator.validate("abc", null);
		assertFalse(errors.isEmpty(), "There should be errors for an invalid password");
		assertTrue(errors.contains(Messages.PASSWORD_TOO_SHORT), "The password too short error should be present");
		assertTrue(errors.contains(Messages.PASSWORD_INSUFFICIENT_DIGIT),
				"The insufficient digit error should be present");
		assertTrue(errors.contains(Messages.PASSWORD_INSUFFICIENT_CHARACTERISTICS),
				"The insufficient characteristics error should be present");
	}

	@Test
	public void validPasswordOnlyUppercaseSatisfiesCharacteristicsRule() {
		List<String> errors = validator.validate("Passw0rd", null);
		assertTrue(errors.isEmpty(), "An uppercase letter should satisfy the characteristics rule");
	}

	@Test
	public void validPasswordOnlySpecialSatisfiesCharacteristicsRule() {
		List<String> errors = validator.validate("passw0rd@", null);
		assertTrue(errors.isEmpty(), "A special character should satisfy the characteristics rule");
	}

	@Test
	public void eachInvalidPasswordProducesAtLeastOneError() {
		String[] invalid = { "foo", "8characs42", "8characs", "manycharactersl0ngpassw0rdbutn0special0rcaps" };
		for (String password : invalid) {
			assertFalse(validator.validate(password, null).isEmpty(),
					password + " should produce at least one error");
		}
	}

	@Test
	public void eachValidPasswordProducesNoErrors() {
		String[] valid = { "8chaRacs", "8cha.acs", "8cha$acs" };
		for (String password : valid) {
			assertTrue(validator.validate(password, null).isEmpty(),
					password + " should produce no errors");
		}
	}

	@Test
	public void returnsListNotNull() {
		List<String> errors = validator.validate("ValidP@ss1", null);
		assertTrue(errors != null, "The return value should not be null");
		assertEquals(0, errors.size(), "The error list should be empty");
	}

}
