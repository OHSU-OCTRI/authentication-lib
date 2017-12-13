package org.octri.authentication.server.security.password;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.octri.authentication.server.security.annotation.ValidPassword;
import org.passay.CharacterCharacteristicsRule;
import org.passay.CharacterRule;
import org.passay.DictionarySubstringRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * A password constraint validator. Validates passwords against OHSU standards.
 * 
 * @see http://www.passay.org/reference/
 * @see http://www.baeldung.com/registration-password-strength-and-rules
 * @author sams
 */
@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public void initialize(ValidPassword arg0) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		System.out.println("siam: validate: password: " + password);
		// Require at least one capital letter
		CharacterRule capitalLetter = new CharacterRule(EnglishCharacterData.UpperCase, 1);

		// Require at least one special character (catches punctuation)
		CharacterRule specialCharacter = new CharacterRule(EnglishCharacterData.Special, 1);

		// Require one of: captial letter, punctuation/special character
		// CharacterCharacteristicsRule allows x out of n rules to be matched
		CharacterCharacteristicsRule capsOrSpecial = new CharacterCharacteristicsRule();
		capsOrSpecial.setNumberOfCharacteristics(1);
		capsOrSpecial.getRules().add(capitalLetter);
		capsOrSpecial.getRules().add(specialCharacter);

		// This prevents words found in the password-blacklist.txt from being in passwords.
		// Use DictionaryRule() to define exact matches.
		DictionarySubstringRule dictionary;
		try {
			Resource blackList = new ClassPathResource("password-blacklist.txt");
			FileReader[] readers = new FileReader[] { new FileReader(blackList.getFile()) };
			ArrayWordList wordList = WordLists.createFromReader(readers, false, new ArraysSort());
			dictionary = new DictionarySubstringRule(new WordListDictionary(wordList));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		// Add all rules to the validator
		PasswordValidator validator = new PasswordValidator(
				Arrays.asList(
						new LengthRule(8, Integer.MAX_VALUE),
						new CharacterRule(EnglishCharacterData.Alphabetical, 1),
						new CharacterRule(EnglishCharacterData.Digit, 1),
						capsOrSpecial,
						dictionary));

		// Validate password
		RuleResult result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			return true;
		}

		return false;
	}

}