package org.octri.authentication.server.security.password;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for allowing admins to generate temporary passwords. This class can be used to disable the
 * feature or to configure the properties of a password.
 * 
 * TODO: Allow configuration of an encoded string used to specify the password format.
 * See @link{StructuredPasswordGenerator} Components. Each component should have a string representation and the format
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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getDictionaryFile() {
		return dictionaryFile;
	}

	public void setDictionaryFile(String dictionaryFile) {
		this.dictionaryFile = dictionaryFile;
	}

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

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
