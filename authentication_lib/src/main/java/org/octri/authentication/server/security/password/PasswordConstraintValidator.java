package org.octri.authentication.server.security.password;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidatorContext;

/**
 * A password constraint validator. Validates passwords against OHSU standards.
 *
 * @see https://ozone.ohsu.edu/cc/sec/isp/00004.pdf
 * @see http://www.passay.org/reference/
 * @see http://www.baeldung.com/registration-password-strength-and-rules
 * @author sams
 */
@Component
public class PasswordConstraintValidator {

	private static final Log log = LogFactory.getLog(PasswordConstraintValidator.class);

	/**
	 * Get a list of validation error messages, or an empty list if the password passes validation.
	 *
	 * @param password
	 * @param context
	 * @return
	 */
	public List<String> validate(String password, ConstraintValidatorContext context) {
		// Require at least one capital letter
		CharacterRule capitalLetter = new CharacterRule(EnglishCharacterData.UpperCase, 1);

		// Require at least one special character (catches punctuation)
		CharacterRule specialCharacter = new CharacterRule(EnglishCharacterData.Special, 1);

		// Require one of: capital letter, punctuation/special character
		// CharacterCharacteristicsRule allows x out of n rules to be matched
		CharacterCharacteristicsRule capsOrSpecial = new CharacterCharacteristicsRule();
		capsOrSpecial.setNumberOfCharacteristics(1);
		capsOrSpecial.getRules().add(capitalLetter);
		capsOrSpecial.getRules().add(specialCharacter);

		// Add all rules to the validator
		PasswordValidator validator = new PasswordValidator(
				Arrays.asList(
						new LengthRule(8, Integer.MAX_VALUE),
						new CharacterRule(EnglishCharacterData.Alphabetical, 1),
						new CharacterRule(EnglishCharacterData.Digit, 1),
						capsOrSpecial));

		// Validate password
		// Note: INSUFFICIENT_CHARACTERISTICS is returned when either INSUFFICIENT_UPPERCASE or INSUFFICIENT_SPECIAL is
		// thrown. These two constraints are handled by {@link CharacterCharacteristicsRule}. Filter them out so we
		// don't duplicate messages in the UI.
		RuleResult result = validator.validate(new PasswordData(password));
		List<String> reasons = result.getDetails().stream().map(RuleResultDetail::getErrorCode)
				.filter(key -> !Reason.ReasonKey.INSUFFICIENT_SPECIAL.toString().equals(key)
						&& !Reason.ReasonKey.INSUFFICIENT_UPPERCASE.toString().equals(key))
				.map(key -> Reason.message(key)).collect(Collectors.toList());

		return reasons;
	}

}