package org.octri.authentication.config;

import org.octri.authentication.server.security.LdapUserDetailsContextMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * Configuration for LDAP authentication.
 */
@Configuration
@EnableConfigurationProperties(LdapContextProperties.class)
@ConditionalOnProperty(value = "octri.authentication.enable-ldap", havingValue = "true", matchIfMissing = false)
public class LdapAuthenticationConfiguration {

	private LdapContextProperties ldapContextProperties;

	/**
	 * Constructor
	 *
	 * @param ldapContextProperties
	 *            LDAP configuration properties
	 */
	public LdapAuthenticationConfiguration(LdapContextProperties ldapContextProperties) {
		this.ldapContextProperties = ldapContextProperties;
	}

	/**
	 * Provides context configuration properties for LDAP authentication.
	 *
	 * @return LDAP configuration properties
	 */
	@Bean
	public LdapContextProperties ldapContextProperties() {
		return ldapContextProperties;
	}

	/**
	 * Provides the configured LDAP organization name.
	 *
	 * @return the configured LDAP organization name
	 */
	@Bean
	public String ldapOrganization() {
		return ldapContextProperties.getOrganization();
	}

	/**
	 * Provides a default context source for LDAP authentication.
	 *
	 * NOTE: Bean must be of type {@link LdapContextSource} to prevent Spring Boot's autoconfiguration from providing a
	 * conflicting bean.
	 *
	 * @return default LDAP context source
	 */
	@Bean
	@ConditionalOnMissingBean
	public LdapContextSource ldapContextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(ldapContextProperties.getUrl());
		contextSource.setUserDn(ldapContextProperties.getUserDn());
		contextSource.setPassword(ldapContextProperties.getPassword());
		return contextSource;
	}

	/**
	 * Provides a default LDAP user search for LDAP authentication.
	 *
	 * @param ldapContextSource
	 *            LDAP context source
	 * @return LDAP search filter for finding user accounts
	 */
	@Bean
	@ConditionalOnMissingBean
	public FilterBasedLdapUserSearch ldapSearch(BaseLdapPathContextSource ldapContextSource) {
		return new FilterBasedLdapUserSearch(ldapContextProperties.getSearchBase(),
				ldapContextProperties.getSearchFilter(), ldapContextSource);
	}

	/**
	 * Provides a default user details mapper for LDAP authentication. By default, user details are loaded from the
	 * database instead of the LDAP directory, even for accounts authenticated using LDAP.
	 *
	 * @param userDetailsService
	 *            service used to look up database user details
	 * @return default LDAP user details mapper
	 */
	@Bean
	@ConditionalOnMissingBean
	public UserDetailsContextMapper ldapContextMapper(UserDetailsService userDetailsService) {
		return new LdapUserDetailsContextMapper(userDetailsService);
	}

	/**
	 * Provides a default authorities populator for LDAP authentication. By default, authorities granted to the user
	 * are loaded from the database instead of the LDAP directory, even for accounts authenticated using LDAP.
	 *
	 * @return default LDAP authorities populator
	 */
	@Bean
	@ConditionalOnMissingBean
	public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
		return new NullLdapAuthoritiesPopulator();
	}

}
