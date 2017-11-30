package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.ApplicationAuthenticationFailureHandler;
import org.octri.authentication.server.security.ApplicationAuthenticationSuccessHandler;
import org.octri.authentication.server.security.AuthenticationUserDetailsService;
import org.octri.authentication.server.security.FormLogoutSuccessHandler;
import org.octri.authentication.server.security.JsonResponseAuthenticationFailureHandler;
import org.octri.authentication.server.security.JsonResponseAuthenticationSuccessHandler;
import org.octri.authentication.server.security.LdapUserDetailsContextMapper;
import org.octri.authentication.server.security.StatusOnlyAuthenticationEntryPoint;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;

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
public class BaseSecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected static final Log log = LogFactory.getLog(BaseSecurityConfiguration.class);

	@Value("${octri.authentication.enable-ldap}")
	protected Boolean enableLdap;

	@Value("${server.context-path}")
	protected String contextPath;

	@Value("${ldap.contextSource.searchBase}")
	protected String ldapSearchBase;

	@Value("${ldap.contextSource.searchFilter}")
	protected String ldapSearchFilter;

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

	@Autowired
	protected FormLogoutSuccessHandler formLogoutSuccessHandler;

	@Bean
	@ConfigurationProperties(prefix = "ldap.contextSource")
	public BaseLdapPathContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		return contextSource;
	}

	@Bean
	public LdapUserDetailsContextMapper ldapContextMapper() {
		return new LdapUserDetailsContextMapper(userDetailsService);
	}

	@Bean
	public TableBasedAuthenticationProvider tableBasedAuthenticationProvider() {
		return new TableBasedAuthenticationProvider(userDetailsService, new BCryptPasswordEncoder());
	}

	/**
	 * Set up authentication.
	 *
	 * @param auth
	 * @throws Exception
	 */
	protected void configureAuth(AuthenticationManagerBuilder auth) throws Exception {
		// Use table-based authentication by default
		auth.userDetailsService(userDetailsService).and().authenticationProvider(tableBasedAuthenticationProvider());

		// Authentication falls through to LDAP if configured
		if (enableLdap) {
			log.info("Enabling LDAP authentication.");
			auth.ldapAuthentication()
					.contextSource(contextSource())
					.userSearchBase(ldapSearchBase)
					.userSearchFilter(ldapSearchFilter)
					.ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
					.userDetailsContextMapper(ldapContextMapper());
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
	 * This is the logout request mapping. Override in your application to change this URL.
	 * 
	 * @return A request mapping e.g. /logout
	 */
	protected String logoutUrl() {
		return "/logout";
	}

	/**
	 * This is the URL a user will be redirected to after a failed login attempt. Override
	 * in your application to change the location.
	 * 
	 * @return A request mapping e.g. /logout?error
	 */
	protected String loginFailureRedirectUrl() {
		return "/login?error";
	}
}
