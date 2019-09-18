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
}
