package org.octri.authentication.server.security.password;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for allowing admins to generate temporary passwords. This class can be used to disable the
 * feature or to configure the properties of a password.
 *
 * TODO: Allow configuration of an encoded string used to specify the password format.
 * See {@link StructuredPasswordGenerator} Components. Each component should have a string representation and the format
 * string should be parsed to construct a component list.
 *
 *
 * @author lawhead
 *
 */
@Component
@ConfigurationProperties(prefix = "octri.authentication.password-gen")
public class PasswordGenConfig {

	private Boolean enabled = false;
	// Located in the dictionaries directory
	private String dictionaryFile = "combined.txt";
	private Integer minWordLength = 4;
	private Integer maxWordLength = 8;
	private String separator = "-";
	// Encoded String; see {@link StructuredPasswordGenerator#setFormat}
	private String format;

	/**
	 * Gets whether password generation is enabled.
	 *
	 * @return true if password generation is enabled, false otherwise
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets whether to enable password generation.
	 *
	 * @param enabled
	 *            true to enable password generation, false if not
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the filename of the dictionary file.
	 *
	 * @return filename of the dictionary file
	 */
	public String getDictionaryFile() {
		return dictionaryFile;
	}

	/**
	 * Sets the filename of the dictionary file.
	 *
	 * @param dictionaryFile
	 *            filename of the dictionary file to use
	 */
	public void setDictionaryFile(String dictionaryFile) {
		this.dictionaryFile = dictionaryFile;
	}

	/**
	 * Gets the minimum word length allowed.
	 *
	 * @return the minimum allowed word length
	 */
	public Integer getMinWordLength() {
		return minWordLength;
	}

	/**
	 * Sets the minimum word length allowed.
	 *
	 * @param minWordLength
	 *            the minimum word length to allow
	 */
	public void setMinWordLength(Integer minWordLength) {
		this.minWordLength = minWordLength;
	}

	/**
	 * Gets the maximum word length allowed.
	 *
	 * @return the maximum allowed word length
	 */
	public Integer getMaxWordLength() {
		return maxWordLength;
	}

	/**
	 * Sets the maximum word length allowed.
	 *
	 * @param maxWordLength
	 *            the maximum word length to allow
	 */
	public void setMaxWordLength(Integer maxWordLength) {
		this.maxWordLength = maxWordLength;
	}

	/**
	 * Gets the string used to join the words in generated passwords.
	 *
	 * @return the separator character
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Sets the string used to join the words in generated passwords.
	 *
	 * @param separator
	 *            the separator character to use
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Gets the password format string.
	 *
	 * @see StructuredPasswordGenerator#setFormat(String)
	 * @return the password format string
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the password format string used.
	 *
	 * @see StructuredPasswordGenerator#setFormat(String)
	 * @param format
	 *            the format to use
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
