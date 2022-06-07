package org.octri.authentication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OctriAuthenticationProperties.class)
public class OctriAuthenticationConfiguration {

	private OctriAuthenticationProperties authenticationProperties;

	public OctriAuthenticationConfiguration(OctriAuthenticationProperties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	@Bean
	public Boolean ldapEnabled() {
		Boolean ldapEnabled = authenticationProperties.getEnableLdap();
		return ldapEnabled != null ? ldapEnabled : Boolean.FALSE;
	}

	@Bean
	public Boolean tableBasedEnabled() {
		Boolean tableBasedEnabled = authenticationProperties.getEnableTableBased();
		return tableBasedEnabled != null ? tableBasedEnabled : Boolean.FALSE;
	}

}
