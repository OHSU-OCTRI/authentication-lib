package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.AdminLogoutSuccessHandler;
import org.octri.authentication.server.security.AuthenticationUserDetailsService;
import org.octri.authentication.server.security.JsonResponseAuthenticationFailureHandler;
import org.octri.authentication.server.security.JsonResponseAuthenticationSuccessHandler;
import org.octri.authentication.server.security.LdapUserDetailsContextMapper;
import org.octri.authentication.server.security.StatusOnlyAuthenticationEntryPoint;
import org.octri.authentication.server.security.StatusOnlyLogoutSuccessHandler;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
 * This class handles all security, AspectJ, and JPA auditing configuration. It extends Spring's
 * {@link WebSecurityConfigurerAdapter} to easily create a
 * {@link org.springframework.security.config.annotation.web.WebSecurityConfigurer} instance.
 * @author sams
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class AbstractSecurityConfiguration extends WebSecurityConfigurerAdapter {

	protected static final Log log = LogFactory.getLog(AbstractSecurityConfiguration.class);

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
	protected JsonResponseAuthenticationSuccessHandler authSuccessHandler;

	@Autowired
	protected JsonResponseAuthenticationFailureHandler authFailureHandler;

	@Autowired
	protected AdminLogoutSuccessHandler adminLogoutSuccessHandler;

	/**
	 * It looks like in Spring Web Security 4 there is already an implementation
	 * that only returns a status:
	 * {@link org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler}
	 */
	@Autowired
	protected StatusOnlyLogoutSuccessHandler logoutSuccessHandler;

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
	 * Used in sub-classes of {@link AbstractSecurityConfiguration}.
	 * @param auth
	 * @throws Exception
	 */
	protected void authentication(AuthenticationManagerBuilder auth) throws Exception {
  		// Use table-based authentication by default
		auth.userDetailsService(userDetailsService)
				.and()
				.authenticationProvider(tableBasedAuthenticationProvider());

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

}
