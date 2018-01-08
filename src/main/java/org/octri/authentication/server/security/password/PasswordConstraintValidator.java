package org.octri.authentication.server.security.password;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.annotation.ValidPassword;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.springframework.stereotype.Component;

/**
 * A password constraint validator. Validates passwords against OHSU standards.
 * 
 * @see https://ozone.ohsu.edu/cc/sec/isp/00004.pdf
 * @see http://www.passay.org/reference/
 * @see http://www.baeldung.com/registration-password-strength-and-rules
 * @author sams
 */
@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	private static final Log log = LogFactory.getLog(PasswordConstraintValidator.class);

	@Override
	public void initialize(ValidPassword arg0) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
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
		RuleResult result = validator.validate(new PasswordData(password));
		for (RuleResultDetail detail : result.getDetails()) {
			log.warn("Password failed validation for reason: " + detail.getErrorCode());
		}
		if (result.isValid()) {
			return true;
		}

		return false;
	}

}