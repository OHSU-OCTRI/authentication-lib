package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * A form security configuration class that is to be used by the application as
 * opposed to a client using the {@link ApiSecurityConfiguration}. OptOut uses
 * the form security configuration whereas an Ember application may use the api
 * security configuration.
 * 
 * @author sams
 */
@Order(10)
public class FormSecurityConfiguration extends BaseSecurityConfiguration {

	protected static final Log log = LogFactory.getLog(FormSecurityConfiguration.class);

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
