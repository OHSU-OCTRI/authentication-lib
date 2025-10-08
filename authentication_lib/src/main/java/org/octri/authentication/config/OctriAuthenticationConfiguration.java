package org.octri.authentication.config;

import static org.octri.authentication.config.OctriAuthenticationProperties.DEFAULT_BASE_URL;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.OctriAuthenticationProperties.RoleStyle;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for the authentication library.
 */
@Configuration
@EnableConfigurationProperties({ AuthenticationRouteProperties.class, ContentSecurityPolicyProperties.class,
		OctriAuthenticationProperties.class })
public class OctriAuthenticationConfiguration {

	private static final Log log = LogFactory.getLog(OctriAuthenticationConfiguration.class);
	private static final String BASE_URL_ERROR = "Table-based authentication is enabled but octri.authentication.base-url is "
			+ DEFAULT_BASE_URL + ". Check the application's configuration.";
	private static final String ACCOUNT_EMAIL_ERROR = "Table-based authentication is enabled but octri.authentication.account-message-email is not set.";

	private static final String[] AUTH_METHOD_PROPERTIES = new String[] {
			"octri.authentication.enable-ldap",
			"octri.authentication.enable-table-based",
			"octri.authentication.saml.enabled"
	};

	private static final String FALLBACK_EMAIL_PROPERTY = "octri.messaging.email.default-sender-address";

	private final String contextPath;
	private final OctriAuthenticationProperties authenticationProperties;

	/**
	 * Constructor.
	 *
	 * @param env
	 *            the Spring application environment
	 * @param authenticationProperties
	 *            authentication configuration
	 * @param contextPath
	 *            the application context path (first URL path segment)
	 */
	public OctriAuthenticationConfiguration(Environment env, OctriAuthenticationProperties authenticationProperties,
			@Value("${server.servlet.context-path:}") String contextPath) {
		this.authenticationProperties = authenticationProperties;
		this.contextPath = contextPath;

		validateAuthenticationMethods(env);
		validateBaseUrl();
		validateRoleStyle();
		validateEmailConfig(env);
	}

	/**
	 * Whether LDAP authentication is enabled.
	 *
	 * @return true if LDAP authentication is enabled
	 */
	@Bean
	public Boolean ldapEnabled() {
		return authenticationProperties.getEnableLdap();
	}

	/**
	 * Whether table-based authentication is enabled.
	 *
	 * @return true if table-based authentication is enabled
	 */
	@Bean
	public Boolean tableBasedEnabled() {
		return authenticationProperties.getEnableTableBased();
	}

	/**
	 * Helper object used to construct the path to authentication-related URLs.
	 *
	 * @return the URL helper
	 */
	@Bean
	public AuthenticationUrlHelper authenticationUrlHelper() {
		return new AuthenticationUrlHelper(authenticationProperties.getBaseUrl(), contextPath);
	}

	/**
	 * Provides a default BCrypt password encoder unless overridden by the application.
	 *
	 * @return default password encoder
	 */
	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder defaultPasswordEncoder() {
		log.debug("No password encoder bean found. Providing default BCrypt encoder.");
		return new BCryptPasswordEncoder();
	}

	/**
	 * Throws an exception unless at least one authentication method has been enabled.
	 *
	 * @param env
	 *            Spring application environment, used to look up property values
	 */
	private void validateAuthenticationMethods(Environment env) {
		String errorMessage = "The authentication_lib requires at least one authentication method to be enabled. "
				+ "Set at least one of: " + String.join(", ", AUTH_METHOD_PROPERTIES);

		log.info("Checking for enabled authentication methods ...");
		boolean anyMethodEnabled = Stream.of(AUTH_METHOD_PROPERTIES)
				.map(propName -> env.getProperty(propName))
				.anyMatch(Boolean::parseBoolean);

		if (!anyMethodEnabled) {
			log.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		} else {
			log.info("At least one authentication method is enabled.");
		}
	}

	/**
	 * Logs an error if table-based authentication is enabled and the base URL property has not been set.
	 */
	private void validateBaseUrl() {
		if (DEFAULT_BASE_URL.equals(authenticationProperties.getBaseUrl())
				&& authenticationProperties.getEnableTableBased()) {
			log.error(BASE_URL_ERROR);
		}
	}

	/**
	 * Validates role style configuration. Logs an error if a custom role validation script is configured, but an
	 * incompatible role style has been selected.
	 */
	private void validateRoleStyle() {
		var scriptErrorMessage = "Property octri.authentication.custom-role-script is configured, but "
				+ "octri.authentication.role-style is not set to \"custom\". Check the application's configuration.";
		var roleStyle = authenticationProperties.getRoleStyle();
		var customRoleScript = authenticationProperties.getCustomRoleScript();

		if (roleStyle != RoleStyle.CUSTOM && customRoleScript != null) {
			log.error(scriptErrorMessage);
		}
	}

	/**
	 * Validates account email configuration. Throws an exception if table-based authentication is enabled and no email
	 * address has been configured.
	 *
	 * @param env
	 *            the Spring application environment
	 */
	private void validateEmailConfig(Environment env) {
		var fallbackEmailAddress = env.getProperty(FALLBACK_EMAIL_PROPERTY);

		if (StringUtils.isBlank(authenticationProperties.getAccountMessageEmail())
				&& authenticationProperties.getEnableTableBased()) {
			if (StringUtils.isNotBlank(fallbackEmailAddress)) {
				log.info("octri.authentication.account-message-email is not set. Falling back to "
						+ FALLBACK_EMAIL_PROPERTY);
				authenticationProperties.setAccountMessageEmail(fallbackEmailAddress);
			} else {
				throw new IllegalStateException(ACCOUNT_EMAIL_ERROR);
			}
		}
	}

}
