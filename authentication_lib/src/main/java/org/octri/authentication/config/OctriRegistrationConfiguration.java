package org.octri.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({OctriRegistrationProperties.class})
public class OctriRegistrationConfiguration {
	private OctriAuthenticationProperties authenticationProperties;

	public OctriRegistrationConfiguration(Environment env, OctriAuthenticationProperties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}
}
