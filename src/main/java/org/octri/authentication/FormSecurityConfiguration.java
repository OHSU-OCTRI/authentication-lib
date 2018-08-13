package org.octri.authentication;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
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
		formAuthSuccessHandler.setDefaultTargetUrl(defaultSuccessUrl());
		formAuthFailureHandler.setDefaultFailureUrl(loginFailureRedirectUrl());
		http.exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				.and()
				.csrf()
				.and()
				.formLogin()
				.permitAll()
				.successHandler(formAuthSuccessHandler)
				.failureHandler(formAuthFailureHandler)
				.and()
				.logout()
				.permitAll()
				.logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl()))
				.logoutSuccessUrl(logoutSuccessUrl())
				.and()
				.authorizeRequests()
				.antMatchers(publicRoutes())
				.permitAll()
				.antMatchers(HttpMethod.POST).authenticated()
				.antMatchers(HttpMethod.PUT).authenticated()
				.antMatchers(HttpMethod.PATCH).authenticated()
				.antMatchers(HttpMethod.DELETE).denyAll()
				.anyRequest()
				.authenticated();
	}

	/**
	 * Adds custom public routes to the default set of public routes. Override
	 * {@link BaseSecurityConfiguration#customPublicRoutes()} in your class that extends
	 * {@link FormSecurityConfiguration} to add additional routes.
	 * 
	 * @return String array of public routes.
	 */
	private String[] publicRoutes() {
		ArrayList<String> allPublicRoutes = new ArrayList<>(Arrays.asList(DEFAULT_PUBLIC_ROUTES));
		allPublicRoutes.addAll(Arrays.asList(customPublicRoutes()));
		return allPublicRoutes.toArray(new String[0]);
	}

}
