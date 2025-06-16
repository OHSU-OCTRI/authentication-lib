package org.octri.authentication.server.security.password;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;

/**
 * Password Generator that generates passwords following a given structure.
 *
 * @author lawhead
 *
 */
public class StructuredPasswordGenerator {

	/**
	 * Password format components
	 */
	public enum Component {

		/**
		 * A word with the first letter capitalized
		 */
		CAPITAL_WORD('C'),

		/**
		 * A lowercase word
		 */
		WORD('W'),

		/**
		 * A digit character
		 */
		DIGIT('D'),

		/**
		 * A special character
		 */
		SPECIAL('S'),

		/**
		 * A symbol character
		 */
		MY_SYMBOL('M');

		private Character code;

		/**
		 * Constructor.
		 *
		 * @param code
		 *            character code used to represent the component
		 */
		Component(Character code) {
			this.code = code;
		}

		/**
		 * @return the character code used to represent the component
		 */
		public Character getCode() {
			return code;
		}

		/**
		 * Find Component by its format code.
		 *
		 * @param code
		 *            character code
		 * @return the component represented by the code, or null if no component is represented by it
		 */
		public static Component fromCode(Character code) {

			for (Component component : Component.values()) {
				if (component.getCode().equals(code)) {
					return component;
				}
			}
			return null;
		}
	}

	private Integer minWordLength = 4;
	private Integer maxWordLength = 8;
	private String symbol = "-";

	private RandomDictionary dictionary;

	// Default password format is valid for OHSU, except for the dictionary-word criteria, which is not enforced.
	private List<Component> format = Arrays.asList(Component.CAPITAL_WORD, Component.MY_SYMBOL, Component.DIGIT,
			Component.DIGIT, Component.DIGIT, Component.MY_SYMBOL, Component.WORD);

	private String specialChars = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

	/**
	 * Construct a new Generator using the provided dictionary.
	 *
	 * @param dictionary
	 *            - dictionary used to retrieve random words.
	 */
	public StructuredPasswordGenerator(RandomDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Generate a random password using the configured format.
	 *
	 * @return a random password in the configured format
	 */
	public String generate() {
		return generate(this.format);
	}

	/**
	 * Generate a random password using the provided format.
	 *
	 * @param format
	 *            format string
	 * @return a random password matching the given format
	 */
	public String generate(List<Component> format) {
		StringBuffer buf = new StringBuffer();

		for (Component component : format) {
			if (component == Component.CAPITAL_WORD) {
				buf.append(randomCapitalizedWord());
			} else if (component == Component.WORD) {
				buf.append(randomWord());
			} else if (component == Component.DIGIT) {
				buf.append(randomDigit());
			} else if (component == Component.SPECIAL) {
				buf.append(randomSymbol());
			} else if (component == Component.MY_SYMBOL) {
				buf.append(symbol);
			}
		}
		return buf.toString();
	}

	/**
	 * @return a random digit (0 - 9)
	 */
	private Integer randomDigit() {
		return ThreadLocalRandom.current().nextInt(0, 10);
	}

	/**
	 * @return a random special character
	 */
	private Character randomSymbol() {
		String syms = specialChars;
		int index = ThreadLocalRandom.current().nextInt(0, syms.length());
		return syms.charAt(index);
	}

	/**
	 * @return a random word from the provided dictionary between minWordLength and maxWordLength and capitalize the
	 *         result.
	 */
	private String randomCapitalizedWord() {
		String word = this.randomWord();
		return word.substring(0, 1).toUpperCase() + word.substring(1);

	}

	/**
	 * @return a random word from the provided dictionary between minWordLength and maxWordLength.
	 */
	private String randomWord() {
		Assert.notNull(this.dictionary, "Dictionary not yet initialized");
		return this.dictionary.getRandom(minWordLength, maxWordLength);
	}

	// Accessors

	/**
	 * Gets the minimum length of selected words.
	 *
	 * @return the minimum length
	 */
	public Integer getMinWordLength() {
		return minWordLength;
	}

	/**
	 * Sets the minimum length of selected words.
	 *
	 * @param minWordLength
	 *            the minimum length
	 */
	public void setMinWordLength(Integer minWordLength) {
		this.minWordLength = minWordLength;
	}

	/**
	 * Gets the maximum length of selected words.
	 *
	 * @return the maximum length
	 */
	public Integer getMaxWordLength() {
		return maxWordLength;
	}

	/**
	 * Sets the maximum length of selected words.
	 *
	 * @param maxWordLength
	 *            the maximum length
	 */
	public void setMaxWordLength(Integer maxWordLength) {
		this.maxWordLength = maxWordLength;
	}

	/**
	 * Gets the symbol string used a separator.
	 *
	 * @return the symbol string
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol string used a separator.
	 *
	 * @param sym
	 *            the symbol string
	 */
	public void setSymbol(String sym) {
		this.symbol = sym;
	}

	/**
	 * Gets the dictionary of words used.
	 *
	 * @return dictionary of words used
	 */
	public RandomDictionary getDictionary() {
		return dictionary;
	}

	/**
	 * Sets the dictionary of words used.
	 *
	 * @param dictionary
	 *            dictionary of words used
	 */
	public void setDictionary(RandomDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Gets the format of generated passwords as a list of password components.
	 *
	 * @return password format
	 */
	public List<Component> getFormat() {
		return format;
	}

	/**
	 * Sets the format of generated passwords as a list of password components.
	 *
	 * @param format
	 *            password format
	 */
	public void setFormat(List<Component> format) {
		this.format = format;
	}

	/**
	 * Set the format from an encoded string; see the Component enumeration for valid codes.
	 *
	 * @param encodedFormat
	 *            password format string
	 */
	public void setFormat(String encodedFormat) {
		List<Component> components = new ArrayList<>();
		for (int i = 0; i < encodedFormat.length(); i++) {
			Character code = encodedFormat.charAt(i);
			Component c = Component.fromCode(code);
			if (c == null) {
				throw new RuntimeException("Invalid password format character: " + code);
			}
			components.add(c);
		}
		this.setFormat(components);
	}

	/**
	 * Gets the list of special characters used, as a string. Default is "!\"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\\]^_`{|}~".
	 *
	 * @return special character string
	 */
	public String getSpecialChars() {
		return specialChars;
	}

	/**
	 * Sets the list of special characters used, as a string. Default is "!\"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\\]^_`{|}~".
	 *
	 * @param specialChars
	 *            special character string
	 */
	public void setSpecialChars(String specialChars) {
		this.specialChars = specialChars;
	}

}
