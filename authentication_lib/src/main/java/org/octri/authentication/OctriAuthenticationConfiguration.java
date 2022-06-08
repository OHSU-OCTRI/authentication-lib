package org.octri.authentication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the authentication library.
 */
@Configuration
@EnableConfigurationProperties(OctriAuthenticationProperties.class)
public class OctriAuthenticationConfiguration {

	private OctriAuthenticationProperties authenticationProperties;

	public OctriAuthenticationConfiguration(OctriAuthenticationProperties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	@Bean
	public Boolean ldapEnabled() {
		return authenticationProperties.getEnableLdap();
	}

	@Bean
	public Boolean tableBasedEnabled() {
		return authenticationProperties.getEnableTableBased();
	}

}
