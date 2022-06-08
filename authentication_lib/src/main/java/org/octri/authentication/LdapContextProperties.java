package org.octri.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the LDAP context source.
 *
 * @see LdapAuthenticationConfiguration.java
 */
@ConfigurationProperties(prefix = "ldap.context-source")
public class LdapContextProperties {

	private String url;
	private String userDn;
	private String password;
	private String searchBase;
	private String searchFilter;
	private String organization;

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

	@Override
	public String toString() {
		return "LdapContextProperties [organization=" + organization + ", password=FILTERED" + ", searchBase="
				+ searchBase + ", searchFilter=" + searchFilter + ", url=" + url + ", userDn=" + userDn + "]";
	}

}
