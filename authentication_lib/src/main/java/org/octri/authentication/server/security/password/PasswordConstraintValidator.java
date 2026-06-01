package org.octri.authentication.server.security.password;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.passay.DefaultPasswordValidator;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResultDetail;
import org.passay.ValidationResult;
import org.passay.data.EnglishCharacterData;
import org.passay.rule.CharacterCharacteristicsRule;
import org.passay.rule.CharacterRule;
import org.passay.rule.LengthRule;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidatorContext;

/**
 * A password constraint validator. Validates passwords against OHSU standards.
 *
 * @see <a href="http://www.passay.org/reference/">Passay reference documentation</a>
 * @see <a href="http://www.baeldung.com/registration-password-strength-and-rules">baeldung.com - Password Strength and
 *      Rules</a>
 * @author sams
 */
@Component
public class PasswordConstraintValidator {

	private static final Log log = LogFactory.getLog(PasswordConstraintValidator.class);

	/**
	 * Get a list of validation error messages, or an empty list if the password passes validation.
	 *
	 * @param password
	 *            password value
	 * @param context
	 *            constraint validation context
	 * @return a list of validation errors, ar an empty list if the password passes validation
	 */
	public List<String> validate(String password, ConstraintValidatorContext context) {
		// Require at least one capital letter
		CharacterRule capitalLetter = new CharacterRule(EnglishCharacterData.UpperCase, 1);

		// Require at least one special character (catches punctuation)
		CharacterRule specialCharacter = new CharacterRule(EnglishCharacterData.Special, 1);

		// Require one of: capital letter, punctuation/special character
		// CharacterCharacteristicsRule allows x out of n rules to be matched
		CharacterCharacteristicsRule capsOrSpecial = new CharacterCharacteristicsRule(1, capitalLetter,
				specialCharacter);

		// Add all rules to the validator
		PasswordValidator validator = new DefaultPasswordValidator(
				Arrays.asList(
						new LengthRule(8, Integer.MAX_VALUE),
						new CharacterRule(EnglishCharacterData.Alphabetical, 1),
						new CharacterRule(EnglishCharacterData.Digit, 1),
						capsOrSpecial));

		// Validate password
		// Note: INSUFFICIENT_CHARACTERISTICS is returned when either INSUFFICIENT_UPPERCASE or INSUFFICIENT_SPECIAL is
		// thrown. These two constraints are handled by {@link CharacterCharacteristicsRule}. Filter them out so we
		// don't duplicate messages in the UI.
		ValidationResult result = validator.validate(new PasswordData(password));
		List<String> reasons = result.getDetails().stream().map(RuleResultDetail::getErrorCode)
				.filter(key -> !Reason.ReasonKey.INSUFFICIENT_SPECIAL.toString().equals(key)
						&& !Reason.ReasonKey.INSUFFICIENT_UPPERCASE.toString().equals(key))
				.map(key -> Reason.message(key)).collect(Collectors.toList());

		return reasons;
	}

}