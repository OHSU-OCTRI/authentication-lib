package org.octri.authentication;

import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.ContentSecurityPolicyProperties;
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.server.security.ApplicationAuthenticationFailureHandler;
import org.octri.authentication.server.security.ApplicationAuthenticationSuccessHandler;
import org.octri.authentication.server.security.AuthenticationUserDetailsService;
import org.octri.authentication.server.security.JsonResponseAuthenticationFailureHandler;
import org.octri.authentication.server.security.JsonResponseAuthenticationSuccessHandler;
import org.octri.authentication.server.security.StatusOnlyAuthenticationEntryPoint;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * A base security configuration class that extends
 * {@link WebSecurityConfigurerAdapter} and sets default annotations for
 * enabling config properties, aspect J auto proxy, JPA auditing, and global
 * method security annotations. It provides LDAP properties and beans for
 * managing authentication. This class should not override
 * {@link #configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}
 * or autowire configureGlobal. Doing so prevents an application using this
 * library from extending one of {@link ApiSecurityConfiguration} or
 * {@link FormSecurityConfiguration} and applying the {@link Configuration}
 * annotation.
 *
 * @author sams
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@EnableJpaAuditing
@PropertySource("classpath:authlib.properties")
@Configuration
public class BaseSecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected static final Log log = LogFactory.getLog(BaseSecurityConfiguration.class);

	protected static final String[] AUTH_METHOD_PROPERTIES = new String[] {
			"octri.authentication.enable-ldap",
			"octri.authentication.enable-table-based"
	};

	protected static final String[] DEFAULT_PUBLIC_ROUTES = new String[] {
			"/",
			"/actuator/health",
			"/actuator/health/liveness",
			"/actuator/health/readiness",
			"/actuator/prometheus",
			"/assets/**",
			"/css/*",
			"/error",
			"/index.html",
			"/js/*",
			"/login*",
			"/login/**",
			"/login*/**",
			"/user/password/**",
			"/webjars/**" };

	@Autowired
	protected Environment env;

	@Autowired
	protected Boolean ldapEnabled;

	@Autowired
	protected Boolean tableBasedEnabled;

	@Autowired
	protected AuthenticationUserDetailsService userDetailsService;

	@Autowired
	protected StatusOnlyAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	protected JsonResponseAuthenticationSuccessHandler jsonAuthSuccessHandler;

	@Autowired
	protected JsonResponseAuthenticationFailureHandler jsonAuthFailureHandler;

	@Autowired
	protected ApplicationAuthenticationSuccessHandler formAuthSuccessHandler;

	@Autowired
	protected ApplicationAuthenticationFailureHandler formAuthFailureHandler;

	@Autowired(required = false)
	protected TableBasedAuthenticationProvider tableBasedAuthenticationProvider;

	@Autowired(required = false)
	protected PasswordEncoder passwordEncoder;

	@Autowired(required = false)
	protected ContentSecurityPolicyProperties cspProperties;

	@Autowired(required = false)
	protected LdapContextProperties ldapContextProperties;

	@Autowired(required = false)
	protected LdapContextSource ldapContextSource;

	@Autowired(required = false)
	protected UserDetailsContextMapper ldapUserDetailsContextMapper;

	@Autowired(required = false)
	protected LdapAuthoritiesPopulator ldapAuthoritiesPopulator;

	/**
	 * Set up authentication.
	 *
	 * @param auth
	 * @throws Exception
	 */
	protected void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
		validateAuthenticationMethods();

		// Use table-based authentication by default
		if (tableBasedEnabled) {
			log.info("Enabling table-based authentication.");
			auth.userDetailsService(userDetailsService).and()
					.authenticationProvider(tableBasedAuthenticationProvider);
		} else {
			log.info("Not enabling table-based authentication: octri.authentication.enable-table-based was false.");
		}

		// Authentication falls through to LDAP if configured
		if (ldapEnabled) {
			log.info("Enabling LDAP authentication.");
			auth.ldapAuthentication()
					.contextSource(ldapContextSource)
					.userSearchBase(ldapContextProperties.getSearchBase())
					.userSearchFilter(ldapContextProperties.getSearchFilter())
					.ldapAuthoritiesPopulator(ldapAuthoritiesPopulator)
					.userDetailsContextMapper(ldapUserDetailsContextMapper);
		} else {
			log.info("Not enabling LDAP authentication: octri.authentication.enable-ldap was false.");
		}
	}

	/**
	 * This method returns the redirect URL for a successful login. Override
	 * in your application to change the location.
	 *
	 * @return A request mapping e.g. /admin/user/list
	 */
	protected String defaultSuccessUrl() {
		return "/admin/user/list";
	}

	/**
	 * This is the login request mapping. Override in your application to change this URL.
	 *
	 * @return A request mapping /login
	 */
	protected String loginUrl() {
		return "/login";
	}

	/**
	 * This is the logout request mapping. Override in your application to change this URL.
	 *
	 * @return A request mapping /logout
	 */
	protected String logoutUrl() {
		return "/logout";
	}

	/**
	 * This is the URL you will be redirected to after logging out successfully. Override in
	 * your application to change this mapping.
	 *
	 * @return A request mapping directing back to /login
	 */
	protected String logoutSuccessUrl() {
		return "/login";
	}

	/**
	 * This is the URL a user will be redirected to after a failed login attempt. Override
	 * in your application to change the location.
	 *
	 * @return A request mapping directing back to /login with the error flag set
	 */
	protected String loginFailureRedirectUrl() {
		return "/login?error";
	}

	/**
	 * Override this method to add public routes to the default set of public routes: {@link #DEFAULT_PUBLIC_ROUTES}
	 *
	 * @return
	 */
	protected String[] customPublicRoutes() {
		return new String[] {};
	}

	/**
	 * Verifies that at least one authentication method is enabled.
	 */
	protected void validateAuthenticationMethods() {
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
}
