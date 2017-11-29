package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * A security configuration class for API clients that do not need redirects or
 * views - only a simple status. The provided alternative is
 * {@link FormSecurityConfiguration} for applications like OptOut where the
 * application is built-in. Use {@link ApiSecurityConfiguration} when a client
 * needs to authenticate with the server as in Ember apps...
 * 
 * TODO: Introduce status only handlers - currently the same as
 * {@link FormSecurityConfiguration}.
 * 
 * @author sams
 */
@Order(20)
public class ApiSecurityConfiguration extends BaseSecurityConfiguration {

	protected static final Log log = LogFactory.getLog(ApiSecurityConfiguration.class);

	/**
	 * Set up authentication.
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		configureAuth(auth);
	}

	/**
	 * Set up basic authentication and restrict requests based on HTTP methods,
	 * URLS, and roles.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		configureHttp(http);
	}

}
