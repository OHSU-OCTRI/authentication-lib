package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;

/**
 * A security configuration class for API clients. Clients do not need redirects
 * or views - only a simple status. The provided alternative is
 * {@link FormSecurityConfiguration} for applications like OptOut where there is
 * now client using an API.
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
		// Use table-based authentication by default
		auth.userDetailsService(userDetailsService).and().authenticationProvider(tableBasedAuthenticationProvider());

		// Authentication falls through to LDAP if configured
		if (enableLdap) {
			log.info("Enabling LDAP authentication.");
			auth.ldapAuthentication().contextSource(contextSource()).userSearchBase(ldapSearchBase)
					.userSearchFilter(ldapSearchFilter).ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
					.userDetailsContextMapper(ldapContextMapper());
		} else {
			log.info("Not enabling LDAP authentication: octri.authentication.enable-ldap was false.");
		}
	}

	/**
	 * Set up basic authentication and restrict requests based on HTTP methods,
	 * URLS, and roles.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and().csrf().and().formLogin()
				.permitAll().defaultSuccessUrl("/admin/user/list").failureHandler(authFailureHandler)
				.failureUrl("/error").and().logout().permitAll().logoutSuccessHandler(adminLogoutSuccessHandler).and()
				.authorizeRequests()
				.antMatchers("/index.html", "/login/**", "/login*", "/login*/**", "/", "/assets/**", "/home/**",
						"/components/**", "/fonts/**")
				.permitAll().antMatchers(HttpMethod.POST).authenticated().antMatchers(HttpMethod.PUT).authenticated()
				.antMatchers(HttpMethod.PATCH).authenticated().antMatchers(HttpMethod.DELETE).denyAll().anyRequest()
				.authenticated();
	}

}
