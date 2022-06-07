package org.octri.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "octri.authentication")
public class OctriAuthenticationProperties {

	private Boolean enableLdap = false;
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
