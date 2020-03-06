package org.octri.authentication.server.security.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.octri.authentication.server.security.password.PasswordGenConfig;
import org.octri.authentication.server.security.password.RandomDictionary;
import org.octri.authentication.server.security.password.StructuredPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Service for generating temporary user passwords.
 * 
 * @author lawhead
 *
 */
@Service
public class PasswordGeneratorService {
	
	@Autowired
	private Boolean tableBasedEnabled;

	private ResourceLoader resourceLoader;
	private StructuredPasswordGenerator generator;
	private Boolean enabled;

	/**
	 * Construct the service using the configured dictionary.
	 * 
	 * @param loader
	 * @throws IOException
	 */
	public PasswordGeneratorService(@Autowired ResourceLoader loader, @Autowired PasswordGenConfig config)
			throws IOException {
		this.resourceLoader = loader;
		// Check that both password generation and table based users are configured
		this.enabled = config.getEnabled() && tableBasedEnabled;

		Resource resource = resourceLoader.getResource(
				"classpath:dictionaries/" + config.getDictionaryFile());
		resource.getInputStream();
		InputStreamReader inputReader = new InputStreamReader(resource.getInputStream());
		try (BufferedReader reader = new BufferedReader(inputReader)) {
			RandomDictionary dictionary = new RandomDictionary(reader.lines().collect(Collectors.toList()));
			this.generator = new StructuredPasswordGenerator(dictionary);
			this.generator.setMinWordLength(config.getMinWordLength());
			this.generator.setMaxWordLength(config.getMaxWordLength());
			this.generator.setSymbol(config.getSeparator());
			if (config.getFormat() != null) {
				this.generator.setFormat(config.getFormat());
			}
		}
	}
	
	/**
	 * Whether this service is enabled allowing generation of temporary passwords
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return a password generated with the provided Generator.
	 */
	public String generatePassword() {
		if (!enabled) {
			throw new UnsupportedOperationException();
		}
		return this.generator.generate();
	}

}
