package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
		http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.and()
				.csrf()
				.and()
				.formLogin()
				.permitAll()
				.defaultSuccessUrl(defaultSuccessUrl())
				.successHandler(formAuthSuccessHandler)
				.failureHandler(formAuthFailureHandler)
				.and()
				.logout()
				.permitAll()
				.logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl()))
				.logoutSuccessHandler(formLogoutSuccessHandler)
				.and()
				.authorizeRequests()
				.antMatchers("/", "/index.html", "/login/**", "/login*", "/login*/**", "/assets/**")
				.permitAll()
				.antMatchers(HttpMethod.POST).authenticated()
				.antMatchers(HttpMethod.PUT).authenticated()
				.antMatchers(HttpMethod.PATCH).authenticated()
				.antMatchers(HttpMethod.DELETE).denyAll()
				.anyRequest()
				.authenticated();
	}

}
