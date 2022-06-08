package org.octri.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the authentication library.
 */
@ConfigurationProperties(prefix = "octri.authentication")
public class OctriAuthenticationProperties {

	/**
	 * Whether LDAP authentication should be enabled.
	 */
	private Boolean enableLdap = false;

	/**
	 * Whether table-based authentication should be enabled.
	 */
	private Boolean enableTableBased = false;

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

	@Override
	public String toString() {
		return "OctriAuthenticationProperties [enableLdap=" + enableLdap + ", enableTableBased=" + enableTableBased
				+ "]";
	}

}
