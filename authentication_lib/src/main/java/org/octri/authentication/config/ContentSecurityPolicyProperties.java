package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for content security policy (CSP).
 *
 * @see <a href=
 *      "https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP">https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP</a>
 */
@ConfigurationProperties(prefix = "octri.authentication.csp")
public class ContentSecurityPolicyProperties {

	/**
	 * The default content security policy. Allows resources from the same origin as the page, and images from the same
	 * origin or inline data: URLs.
	 */
	public static final String DEFAULT_POLICY = "default-src 'self'; img-src 'self' data:";

	/**
	 * Whether to enable the <code>Content-Security-Policy</code> header. Default: false.
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

	/**
	 * Gets whether the <code>Content-Security-Policy</code> header is enabled.
	 * 
	 * @return whether the CSP header is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets whether to enable the <code>Content-Security-Policy</code> header.
	 * 
	 * @param enabled
	 *            true to enable the header, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets whether the configured policy is enforced, or if policy violations are only logged.
	 * 
	 * @return whether CSP policy is enforced
	 */
	public boolean isEnforced() {
		return enforced;
	}

	/**
	 * Sets whether to enforce the policy, or if policy violations should only be logged.
	 * 
	 * @param enforced
	 *            true to enforce the policy, false to log only
	 */
	public void setEnforced(boolean enforced) {
		this.enforced = enforced;
	}

	/**
	 * Gets the configured policy string.
	 * 
	 * @return value of the current policy
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * Set the content security policy string.
	 * 
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(String policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return "ContentSecurityPolicyProperties [enabled=" + enabled + ", enforced=" + enforced + ", policy=" + policy
				+ "]";
	}

}
