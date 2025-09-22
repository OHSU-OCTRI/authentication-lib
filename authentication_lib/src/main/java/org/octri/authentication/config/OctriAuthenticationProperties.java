package org.octri.authentication.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;

import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for the authentication library.
 */
@ConfigurationProperties(prefix = "octri.authentication")
public class OctriAuthenticationProperties {

	/**
	 * Allowed username styles. Valid options are PLAIN (simple username), EMAIL (must be an email address), or MIXED
	 * (may be either a plain username or an email adress).
	 */
	public static enum UsernameStyle {
		/**
		 * Simple username style. May not be an email address.
		 */
		PLAIN,

		/**
		 * Username must be an email address.
		 */
		EMAIL,

		/**
		 * Username may be either a plain username or an email address.
		 */
		MIXED
	}

	/**
	 * The style of roles allowed. Valid options are SINGLE (users may have only one role), MULTIPLE (a user may have
	 * any number of roles), or CUSTOM (application-specific behavior).
	 */
	public static enum RoleStyle {
		/**
		 * Users may only have one role.
		 */
		SINGLE,

		/**
		 * A user may have any number of roles.
		 */
		MULTIPLE,

		/**
		 * Role behavior is application-specific.
		 */
		CUSTOM
	}

	/**
	 * The default value of "octri.authentication.base-url".
	 */
	public static final String DEFAULT_BASE_URL = "http://localhost:8080";

	/**
	 * The default value in minutes for "octri.authentication.password-token-validity"
	 */
	public static final Duration DEFAULT_PASSWORD_TOKEN_DURATION = Duration.ofMinutes(30);

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

	/**
	 * Style of usernames allowed. Valid options are PLAIN (simple username), EMAIL (must be an email address), or MIXED
	 * (may be either a plain username or an email address). Defaults to PLAIN.
	 */
	private UsernameStyle usernameStyle = UsernameStyle.PLAIN;

	/**
	 * Length of time that password tokens will be valid for
	 */
	@DurationUnit(ChronoUnit.MINUTES)
	private Duration passwordTokenValidFor = DEFAULT_PASSWORD_TOKEN_DURATION;

	/**
	 * Whether the email field on the user form is required.
	 */
	private Boolean emailRequired = true;

	/**
	 * Whether email should be sent or logged to the server instead.
	 *
	 * @deprecated
	 *             Use `octri.messaging.email-delivery-method=log` to to log messages instead.
	 */
	@Deprecated(since = "2.3.0", forRemoval = true)
	private Boolean emailDryRun = false;

	/**
	 * The style of roles allowed. This determines how the role selector on the user form is rendered. Valid options are
	 * SINGLE (users may have only one role), MULTIPLE (a user may have any number of roles), or CUSTOM
	 * (application-specific behavior). Defaults to MULTIPLE.
	 */
	@NotNull
	private RoleStyle roleStyle = RoleStyle.MULTIPLE;

	/**
	 * Path to a JavaScript file to use when "octri.authentication.role-style" is "custom". This path should be relative
	 * to the application's context path. Only relevant when "octri.authentication.role-style" is "custom".
	 */
	private String customRoleScript;

	/**
	 * Gets whether LDAP authentication is enabled.
	 *
	 * @return true if LDAP authentication is enabled, false if not
	 */
	public Boolean getEnableLdap() {
		return enableLdap;
	}

	/**
	 * Sets whether to enable LDAP authentication.
	 *
	 * @param enableLdap
	 *            true to enable LDAP authentication, false if not
	 */
	public void setEnableLdap(Boolean enableLdap) {
		this.enableLdap = enableLdap;
	}

	/**
	 * Gets whether table-based authentication is enabled.
	 *
	 * @return true if table-based authentication is enabled, false if not
	 */
	public Boolean getEnableTableBased() {
		return enableTableBased;
	}

	/**
	 * Sets whether to enable table-based authentication.
	 *
	 * @param enableTableBased
	 *            true to enable table-based authentication, false if not
	 */
	public void setEnableTableBased(Boolean enableTableBased) {
		this.enableTableBased = enableTableBased;
	}

	/**
	 * Gets the base URL of the application without a context path. Used to construct links sent in emails.
	 *
	 * @return the application's base URL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Sets the base URL of the application, including the host, port, and scheme. Does not include the context path.
	 *
	 * @param baseUrl
	 *            the application's base URL
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Gets the maximum number of failed login attempts before accounts are locked.
	 *
	 * @return the number of allowed login attempts
	 */
	public Integer getMaxLoginAttempts() {
		return maxLoginAttempts;
	}

	/**
	 * Gets the maximum number of failed login attempts before accounts are locked.
	 *
	 * @param maxLoginAttempts
	 *            the number of login attempts to allow
	 */
	public void setMaxLoginAttempts(Integer maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	/**
	 * Gets the length of time (in days) that table-based credentials are valid. After this period has elapsed, users
	 * will be required to change their password.
	 *
	 * @return the number of days that table-based passwords will be valid
	 */
	public Integer getCredentialsExpirationPeriod() {
		return credentialsExpirationPeriod;
	}

	/**
	 * Sets the length of time (in days) that table-based credentials will be valid. After this period has elapsed,
	 * users will be required to change their password.
	 *
	 * @param credentialsExpirationPeriod
	 *            the number of days that table-based passwords should be valid
	 */
	public void setCredentialsExpirationPeriod(Integer credentialsExpirationPeriod) {
		this.credentialsExpirationPeriod = credentialsExpirationPeriod;
	}

	/**
	 * Gets the allowed username style.
	 *
	 * @see UsernameStyle
	 * @return the type of usernames allowed
	 */
	public UsernameStyle getUsernameStyle() {
		return usernameStyle;
	}

	/**
	 * Sets the allowed username style.
	 *
	 * @see UsernameStyle
	 * @param usernameStyle
	 *            the type of usernames to allow
	 */
	public void setUsernameStyle(UsernameStyle usernameStyle) {
		this.usernameStyle = usernameStyle;
	}

	/**
	 * Sets the length of time that password reset tokens will be valid for.
	 *
	 * @param validityTime
	 *            how long password tokens will remain valid
	 */
	public void setPasswordTokenValidFor(Duration validityTime) {
		this.passwordTokenValidFor = validityTime;
	}

	/**
	 * Gets the length of time that password reset tokens will be valid for.
	 *
	 * @return how long password reset tokens are valid
	 */
	public Duration getPasswordTokenValidFor() {
		return passwordTokenValidFor;
	}

	/**
	 * Gets whether email will be logged to the console or actually delivered.
	 *
	 * @return true if email will be logged to the console, false if it will be sent
	 */
	public Boolean getEmailDryRun() {
		return emailDryRun;
	}

	/**
	 * Sets whether email should be logged to the console.
	 *
	 * @param emailDryRun
	 *            true if email should be logged to the console instead of sending, false to deliver email
	 */
	@DeprecatedConfigurationProperty(since = "2.3.0", reason = "Replaced by email delivery strategy", replacement = "octri.messaging.email-delivery-method")
	public void setEmailDryRun(Boolean emailDryRun) {
		this.emailDryRun = emailDryRun;
	}

	/**
	 * Gets whether user accounts are required to have an email address.
	 *
	 * @return true if users are required to have an email address
	 */
	public Boolean getEmailRequired() {
		return emailRequired;
	}

	/**
	 * Sets whether user accounts are required to have an email address.
	 *
	 * @param emailRequired
	 *            true to require an email address, false if not
	 */
	public void setEmailRequired(Boolean emailRequired) {
		this.emailRequired = emailRequired;
	}

	/**
	 * Gets the style of roles allowed.
	 *
	 * @see RoleStyle
	 * @return the style of user roles allowed
	 */
	public RoleStyle getRoleStyle() {
		return roleStyle;
	}

	/**
	 * Sets the style of roles allowed.
	 *
	 * @see RoleStyle
	 * @param roleStyle
	 *            the style of user roles that should be allowed
	 */
	public void setRoleStyle(RoleStyle roleStyle) {
		this.roleStyle = roleStyle;
	}

	/**
	 * Gets the path to the JavaScript file used when "octri.authentication.role-style" is "custom".
	 *
	 * @return the path to the JavaScript file
	 */
	public String getCustomRoleScript() {
		return customRoleScript;
	}

	/**
	 * Sets the path to the JavaScript file used when "octri.authentication.role-style" is "custom". This path should be
	 * relative to the application's context path.
	 *
	 * @param customRoleScript
	 *            path of the JavaScript file
	 */
	public void setCustomRoleScript(String customRoleScript) {
		this.customRoleScript = customRoleScript;
	}

	@Override
	public String toString() {
		return "OctriAuthenticationProperties [enableLdap=" + enableLdap + ", enableTableBased=" + enableTableBased
				+ ", baseUrl=" + baseUrl + ", maxLoginAttempts=" + maxLoginAttempts + ", credentialsExpirationPeriod="
				+ credentialsExpirationPeriod + ", usernameStyle=" + usernameStyle + ", passwordTokenValidFor="
				+ passwordTokenValidFor + ", emailRequired=" + emailRequired + ", emailDryRun=" + emailDryRun
				+ ", roleStyle=" + roleStyle + ", customRoleScript=" + customRoleScript + "]";
	}

}
