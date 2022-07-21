package org.octri.authentication.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for table-based authentication.
 */
@Configuration
@ConditionalOnProperty(value = "octri.authentication.enable-table-based", havingValue = "true", matchIfMissing = false)
public class TableBasedAuthenticationConfiguration {

	private static final Log log = LogFactory.getLog(TableBasedAuthenticationConfiguration.class);

	/**
	 * Provides a default BCrypt password encoder for table-based accounts.
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder defaultPasswordEncoder() {
		log.debug("No password encoder bean found. Providing default BCrypt encoder.");
		return new BCryptPasswordEncoder();
	}

	/**
	 * Provides a default authentication provider for table-based accounts.
	 *
	 * @param userDetailsService
	 * @param passwordEncoder
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public TableBasedAuthenticationProvider tableBasedAuthenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		log.debug("Creating default table-based authentication provider.");
		return new TableBasedAuthenticationProvider(userDetailsService, passwordEncoder);
	}

}
