package org.octri.authentication.server.security.password;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.octri.authentication.server.security.password.StructuredPasswordGenerator.Component;

/**
 * Tests for StructuredPasswordGenerator
 * 
 * @author lawhead
 *
 */
public class StructuredPasswordGeneratorTest {

	List<String> words = new ArrayList<String>(Arrays.asList("foo", "bar", "baz", "four", "eagle", "wizard", "bread"));

	RandomDictionary dict = new RandomDictionary(words);
	StructuredPasswordGenerator generator = new StructuredPasswordGenerator(dict);

	PasswordConstraintValidator passwordConstraintValidator = new PasswordConstraintValidator();

	@Test
	public void testDefault() {
		generator.setMaxWordLength(6);
		String password = generator.generate();

		// Structure notes:
		// CW = capitalized word, W = word, D = digit, Sp = special, Sym = known symbol
		// default format is [CW, Sym, D, D, D, Sym, W]
		assertTrue(generator.getDictionary() != null);
		assertTrue(password.length() >= 14 && password.length() <= 18);

		assertTrue(password + " should be valid", passwordConstraintValidator.validate(password, null).isEmpty());

		// There is only 1 word with length 4.
		generator.setMinWordLength(4);
		generator.setMaxWordLength(4);
		password = generator.generate();

		assertTrue(password.length() == 13);
		assertTrue(password.startsWith("Four"));
		assertTrue(password.endsWith("four"));
	}

	@Test
	public void testCustomFormat() {
		List<Component> format = Arrays.asList(Component.MY_SYMBOL, Component.MY_SYMBOL, Component.MY_SYMBOL);
		generator.setSymbol("#");

		String password = generator.generate(format);
		assertEquals(password, "###");

		generator.setMinWordLength(3);
		generator.setMaxWordLength(3);

		password = generator
				.generate(Arrays.asList(Component.WORD, Component.CAPITAL_WORD, Component.CAPITAL_WORD));
		assertEquals(password.length(), 9);
	}

	@Test
	public void testSpecialChars() {
		List<Component> format = Arrays.asList(Component.WORD, Component.DIGIT);
		generator.setMinWordLength(6);
		generator.setMaxWordLength(6);

		String password = generator.generate(format);

		assertTrue(password.length() == 7);

		assertFalse(intersects(password, generator.getSpecialChars()));

		format = Arrays.asList(Component.WORD, Component.SPECIAL, Component.DIGIT);
		password = generator.generate(format);
		assertTrue(password.length() == 8);
		assertTrue(intersects(password, generator.getSpecialChars()));
	}

	/**
	 * 
	 * @param string1
	 * @param string2
	 * @return true if the two strings have any characters in common.
	 */
	private Boolean intersects(String string1, String string2) {

		for (int i = 0; i < string1.length(); i++) {
			for (int j = 0; j < string2.length(); j++) {
				if (string1.charAt(i) == string2.charAt(j)) {
					return true;
				}
			}
		}
		return false;
	}
}
