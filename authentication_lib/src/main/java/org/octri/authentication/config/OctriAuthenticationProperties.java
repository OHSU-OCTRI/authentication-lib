package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the authentication library.
 */
@ConfigurationProperties(prefix = "octri.authentication")
public class OctriAuthenticationProperties {

	/**
	 * The default value of "octri.authentication.base-url".
	 */
	public static final String DEFAULT_BASE_URL = "http://localhost:8080";

	/**
	 * Whether LDAP authentication should be enabled.
	 */
	private Boolean enableLdap = false;

	/**
	 * Whether table-based authentication should be enabled.
	 */
	private Boolean enableTableBased = false;

	/**
	 * The base URL of the application without a context path. Used to construct links sent in emails. Defaults to
	 * the value of @link{OctriAuthenticationProperties.DEFAULT_BASE_URL}.
	 */
	private String baseUrl = DEFAULT_BASE_URL;

	/**
	 * Number of failed login attempts allowed before accounts are locked.
	 */
	private Integer maxLoginAttempts = 7;

	/**
	 * Length of time (in days) that credentials are valid. After this period has elapsed, users will be required
	 * to change their password.
	 */
	private Integer credentialsExpirationPeriod = 180;

	public Boolean getEnableLdap() {
		return enableLdap;
	}

	public void setEnableLdap(Boolean enableLdap) {
		this.enableLdap = enableLdap;
	}

	public Boolean getEnableTableBased() {
		return enableTableBased;
	}

	public void setEnableTableBased(Boolean enableTableBased) {
		this.enableTableBased = enableTableBased;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Integer getMaxLoginAttempts() {
		return maxLoginAttempts;
	}

	public void setMaxLoginAttempts(Integer maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	public Integer getCredentialsExpirationPeriod() {
		return credentialsExpirationPeriod;
	}

	public void setCredentialsExpirationPeriod(Integer credentialsExpirationPeriod) {
		this.credentialsExpirationPeriod = credentialsExpirationPeriod;
	}

	@Override
	public String toString() {
		return "OctriAuthenticationProperties [baseUrl=" + baseUrl + ", credentialsExpirationPeriod="
				+ credentialsExpirationPeriod + ", enableLdap=" + enableLdap + ", enableTableBased=" + enableTableBased
				+ ", maxLoginAttempts=" + maxLoginAttempts + "]";
	}

}
