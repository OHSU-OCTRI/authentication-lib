package org.octri.authentication;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for content security policy (CSP).
 *
 * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP
 */
@ConfigurationProperties(prefix = "octri.authentication.csp")
public class ContentSecurityPolicyProperties {

	public static final String DEFAULT_POLICY = "default-src 'self'; img-src 'self' data:";

	/**
	 * Whether to enable the `Content-Security-Policy` header. Default: false.
	 */
	private boolean enabled = false;

	/**
	 * Whether to enforce the configured policy. If true, the browser will not load assets that violate the policy. If
	 * false, violations will be logged, but assets will still be loaded. Default: false.
	 */
	private boolean enforced = false;

	/**
	 * Policy string. Default: {@link ContentSecurityPolicyProperties.DEFAULT_POLICY}.
	 */
	private String policy = DEFAULT_POLICY;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnforced() {
		return enforced;
	}

	public void setEnforced(boolean enforced) {
		this.enforced = enforced;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return "ContentSecurityPolicyProperties [enabled=" + enabled + ", enforced=" + enforced + ", policy=" + policy
				+ "]";
	}

}
