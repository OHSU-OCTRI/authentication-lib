package org.octri.authentication;

import static org.octri.authentication.OctriAuthenticationProperties.DEFAULT_BASE_URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the authentication library.
 */
@Configuration
@EnableConfigurationProperties({ OctriAuthenticationProperties.class, ContentSecurityPolicyProperties.class })
public class OctriAuthenticationConfiguration {

	private static final Log log = LogFactory.getLog(OctriAuthenticationConfiguration.class);
	private static final String BASE_URL_ERROR = "Table-based authentication is enabled but octri.authentication.base-url is "
			+ DEFAULT_BASE_URL + ". Check the application's configuration.";

	private String contextPath;
	private OctriAuthenticationProperties authenticationProperties;

	public OctriAuthenticationConfiguration(OctriAuthenticationProperties authenticationProperties,
			@Value("${server.servlet.context-path:}") String contextPath) {
		this.authenticationProperties = authenticationProperties;
		this.contextPath = contextPath;

		if (DEFAULT_BASE_URL.equals(authenticationProperties.getBaseUrl())
				&& authenticationProperties.getEnableTableBased()) {
			log.error(BASE_URL_ERROR);
		}
	}

	@Bean
	public Boolean ldapEnabled() {
		return authenticationProperties.getEnableLdap();
	}

	@Bean
	public Boolean tableBasedEnabled() {
		return authenticationProperties.getEnableTableBased();
	}

	@Bean
	public AuthenticationUrlHelper authenticatonUrlHelper() {
		return new AuthenticationUrlHelper(authenticationProperties.getBaseUrl(), contextPath);
	}

}
