package org.octri.authentication.server.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

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
	 */
	public TableBasedAuthenticationProvider(UserDetailsService userDetailsService) {
		super(userDetailsService);
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		return super.authenticate(authentication);
	}
}
