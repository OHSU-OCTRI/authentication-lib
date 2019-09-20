package org.octri.authentication.server.security.password;

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

	public enum Component {
		CAPITAL_WORD, WORD, DIGIT, SPECIAL, MY_SYMBOL
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
	 * @return
	 */
	public String generate() {
		return generate(this.format);
	}

	/**
	 * Generate a random password using the provided format.
	 * 
	 * @param format
	 * @return
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
	 * 
	 * @return a random digit (0 - 9)
	 */
	private Integer randomDigit() {
		return ThreadLocalRandom.current().nextInt(0, 10);
	}

	/**
	 * 
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
	public Integer getMinWordLength() {
		return minWordLength;
	}

	public void setMinWordLength(Integer minWordLength) {
		this.minWordLength = minWordLength;
	}

	public Integer getMaxWordLength() {
		return maxWordLength;
	}

	public void setMaxWordLength(Integer maxWordLength) {
		this.maxWordLength = maxWordLength;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String sym) {
		this.symbol = sym;
	}

	public RandomDictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(RandomDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public List<Component> getFormat() {
		return format;
	}

	public void setFormat(List<Component> format) {
		this.format = format;
	}

	public String getSpecialChars() {
		return specialChars;
	}

	public void setSpecialChars(String specialChars) {
		this.specialChars = specialChars;
	}

}
