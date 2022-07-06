package org.octri.authentication;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the LDAP context source.
 *
 * @see LdapAuthenticationConfiguration.java
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserDn() {
		return userDn;
	}

	public void setUserDn(String userDn) {
		this.userDn = userDn;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSearchBase() {
		return searchBase;
	}

	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getEmailDomain() {
		return emailDomain;
	}

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
