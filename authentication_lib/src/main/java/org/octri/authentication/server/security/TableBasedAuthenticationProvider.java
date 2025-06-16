package org.octri.authentication.server.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author harrelst
 */
public class TableBasedAuthenticationProvider extends DaoAuthenticationProvider {

	Log log = LogFactory.getLog(TableBasedAuthenticationProvider.class);

	/**
	 * Constructor.
	 *
	 * @param userDetailsService
	 *            service used to look up user details
	 * @param passwordEncoder
	 *            encodes passwords for storage in the database
	 */
	public TableBasedAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.setUserDetailsService(userDetailsService);
		this.setPasswordEncoder(passwordEncoder);
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		return super.authenticate(authentication);
	}
}
