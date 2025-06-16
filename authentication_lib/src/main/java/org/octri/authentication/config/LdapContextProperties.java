package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for the LDAP context source.
 *
 * @see LdapAuthenticationConfiguration
 */
@Validated
@ConfigurationProperties(prefix = "ldap.context-source")
public class LdapContextProperties {

	/**
	 * URL of the LDAP directory.
	 */
	@NotNull
	private String url;

	/**
	 * User DN to use to connect to the directory.
	 */
	@NotNull
	private String userDn;

	/**
	 * Password to use to connect to the directory.
	 */
	@NotNull
	private String password;

	/**
	 * Base DN (subtree) to search for user accounts.
	 */
	@NotNull
	private String searchBase;

	/**
	 * Filter to use to search for user accounts.
	 */
	@NotNull
	private String searchFilter;

	/**
	 * LDAP user organization. Used to automatically populate the user's `institution` property.
	 */
	@NotNull
	private String organization;

	/**
	 * Email domain to use to identify LDAP users.
	 */
	@NotNull
	private String emailDomain;

	/**
	 * Gets the URL of the LDAP directory.
	 * 
	 * @return LDAP directory URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL of the LDAP directory.
	 * 
	 * @param url
	 *            LDAP directory URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the user DN used to bind to the directory.
	 * 
	 * @return user DN
	 */
	public String getUserDn() {
		return userDn;
	}

	/**
	 * Sets the user DN used to bind to the directory.
	 * 
	 * @param userDn
	 *            user DN
	 */
	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	/**
	 * Gets the password used to bind to the directory.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password used to bind to the directory.
	 * 
	 * @param password
	 *            the password to use
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the base DN (subtree) searched for user accounts.
	 * 
	 * @return the base DN
	 */
	public String getSearchBase() {
		return searchBase;
	}

	/**
	 * Sets the base DN (subtree) searched for user accounts.
	 * 
	 * @param searchBase
	 *            the base DN to search
	 */
	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	/**
	 * Gets the filter used to search for user accounts.
	 * 
	 * @return user search filter
	 */
	public String getSearchFilter() {
		return searchFilter;
	}

	/**
	 * Sets the filter used to search for user accounts.
	 * 
	 * @param searchFilter
	 *            user search filter
	 */
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * Gets the LDAP user organization used to populate the user's "institution" property.
	 * 
	 * @return the LDAP user organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Sets the LDAP user organization used to populate the user's "institution" property.
	 * 
	 * @param organization
	 *            the LDAP user organization
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * Gets the email domain used to identify LDAP accounts.
	 * 
	 * @return the LDAP email domain
	 */
	public String getEmailDomain() {
		return emailDomain;
	}

	/**
	 * Sets the email domain used to identify LDAP accounts.
	 * 
	 * @param emailDomain
	 *            an email domain
	 */
	public void setEmailDomain(String emailDomain) {
		this.emailDomain = emailDomain;
	}

	@Override
	public String toString() {
		return "LdapContextProperties [emailDomain=" + emailDomain + ", organization=" + organization
				+ ", password=FILTERED" + ", searchBase=" + searchBase + ", searchFilter=" + searchFilter + ", url="
				+ url + ", userDn=" + userDn + "]";
	}

}
