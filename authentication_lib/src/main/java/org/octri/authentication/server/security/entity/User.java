package org.octri.authentication.server.security.entity;

import static org.octri.authentication.utils.ValidationUtils.VALID_EMAIL_REGEX;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.octri.authentication.server.view.Labelled;
import org.octri.authentication.validation.Emailable;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents data related to a user account.
 *
 * @author yateam
 *
 */
@Entity
public class User extends AbstractEntity implements Labelled {

	private static final String INVALID_EMAIL_MESSAGE = "Please provide a valid email address";

	/**
	 * Default constructor.
	 */
	public User() {
		super();
		setDefaults();
	}

	/**
	 * Set default values. Used in each constructor.
	 */
	private void setDefaults() {
		// enabled with credentials expired by default
		this.enabled = true;
		this.accountLocked = false;
		this.consecutiveLoginFailures = 0;
	}

	/**
	 * The username used to identify the user.
	 */
	@Column(unique = true)
	@NotNull(message = "Username is required")
	@Size(max = 320, min = 1, message = "Username must be 1-320 characters")
	private String username;

	/**
	 * Password hash.
	 */
	private String password;

	/**
	 * Whether the account is enabled for login.
	 */
	@NotNull(message = "Enabled must be Yes or No")
	private Boolean enabled;

	/**
	 * Whether the account has been locked by failed login attempts.
	 */
	@NotNull(message = "Account locked must be Yes or No")
	private Boolean accountLocked;

	/**
	 * The user's first name (given name).
	 */
	@NotNull(message = "First name is required")
	@Size(max = 50, min = 1, message = "First name must be 1-50 characters")
	protected String firstName;

	/**
	 * The user's last name (family name, surname).
	 */
	@NotNull(message = "Last name is required")
	@Size(max = 50, min = 1, message = "Last name must be 1-50 characters")
	protected String lastName;

	/**
	 * The institution the user is associated with. Optional, primarily for use with LDAP.
	 */
	@Size(max = 100, min = 1, message = "Institution must be 1-50 characters")
	private String institution;

	/**
	 * The user's email address.
	 *
	 * @see <a href=
	 *      "http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-builtin-constraints">Built-in
	 *      constraints</a>
	 */
	@Email(message = INVALID_EMAIL_MESSAGE, regexp = VALID_EMAIL_REGEX, groups = Emailable.class)
	@NotNull(message = "Email is required", groups = Emailable.class)
	@Size(max = 320, min = 1, message = "Email must be between 1 and 320 characters", groups = Emailable.class)
	private String email;

	/**
	 * The number of failed login attempts since the last successful login.
	 */
	private Integer consecutiveLoginFailures;

	/**
	 * Timestamp when the account will expire. After this timestamp, login will not be allowed.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date accountExpirationDate;

	/**
	 * Timestamp when the user's credentials will expire. After this timestamp, their password must be changed.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date credentialsExpirationDate;

	/**
	 * User roles assigned the user has been assigned.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_user_role", joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_role", referencedColumnName = "id"))
	private List<UserRole> userRoles;

	/**
	 * The method used to authenticate the user.
	 */
	@Enumerated(EnumType.STRING)
	@NotNull
	private AuthenticationMethod authenticationMethod;

	/**
	 * Gets whether the user account is allowed to log in.
	 *
	 * @return true if enabled, false if not
	 */
	public boolean isEnabled() {
		if (this.enabled != null && this.enabled) {
			return true;
		}
		return false;
	}

	/**
	 * Sets whether the user account is allowed to log in.
	 *
	 * @see #setEnabled(Boolean)
	 * @param enabled
	 *            true to enable login, false if not
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets whether the account is locked because of failed login attempts.
	 *
	 * @return true if locked, false if not
	 */
	public Boolean getAccountLocked() {
		if (accountLocked == null) {
			return false;
		}
		return accountLocked;
	}

	/**
	 * Gets the number of failed login attempts since the last successful login.
	 *
	 * @return the number of login failures
	 */
	public Integer getConsecutiveLoginFailures() {
		if (consecutiveLoginFailures == null) {
			return 0;
		}
		return consecutiveLoginFailures;
	}

	/**
	 * Method use to convert a {@link Date} into a date string of format "MM/dd/yyyy". This is used
	 * where you need to display a date in the UI for Hibernate fields of type timestamp.
	 *
	 * @param timestamp
	 *            the date to convert
	 * @return a string representation of Date, or empty string if timestamp is null.
	 */
	private String timestampToDateString(Date timestamp) {
		if (timestamp == null) {
			return "";
		}
		return Instant.ofEpochMilli(timestamp.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
				.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	/**
	 * Gets the username used to log in.
	 *
	 * @return the user's username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the username used to log in.
	 *
	 * @param username
	 *            the user's username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password hash for table-based authentication.
	 *
	 * @return a hashed password value
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the password hash for table-based authentication.
	 *
	 * @param password
	 *            a hashed password value
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets whether the account is allowed to log in.
	 *
	 * @see #isEnabled()
	 * @return true if enabled, false if not
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets whether the account is allowed to log in.
	 *
	 * @see #setEnabled(boolean)
	 * @param enabled
	 *            true to enable, false if not
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the user's first name (given name).
	 *
	 * @return the user's first name
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Sets the user's first name (given name).
	 *
	 * @param firstName
	 *            the user's first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the user's last name (family name, surname).
	 *
	 * @return the user's last name
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets the user's last name (family name, surname).
	 *
	 * @param lastName
	 *            the user's last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the institution the user is associated with (optional, primarily for LDAP accounts).
	 *
	 * @return the user's institution
	 */
	public String getInstitution() {
		return this.institution;
	}

	/**
	 * Sets the institution the user is associated with (optional, primarily for LDAP accounts).
	 *
	 * @param institution
	 *            the user's institution
	 */
	public void setInstitution(String institution) {
		this.institution = institution;
	}

	/**
	 * Gets the user's email address.
	 *
	 * @return the user's email address
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the user's email address.
	 *
	 * @param email
	 *            the user's email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets whether the account has been locked because of failed login attempts.
	 *
	 * @param accountLocked
	 *            true if the account is locked, false if not
	 */
	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	/**
	 * Gets whether the user account has expired ({@link #accountExpirationDate} is in the past).
	 *
	 * @return true if the account has expired, false if not
	 */
	public Boolean getAccountExpired() {
		return accountExpirationDate != null && accountExpirationDate.before(Date.from(Instant.now()));
	}

	/**
	 * Gets whether the user's credentials have expired ({@link #credentialsExpirationDate} is in the past).
	 *
	 * @return true if the user's credentials have expired, false if not
	 */
	public Boolean getCredentialsExpired() {
		return credentialsExpirationDate != null && credentialsExpirationDate.before(Date.from(Instant.now()));
	}

	/**
	 * Sets the count of failed login attempts since the last successful login.
	 *
	 * @param consecutiveLoginFailures
	 *            the count of failed attempts
	 */
	public void setConsecutiveLoginFailures(Integer consecutiveLoginFailures) {
		this.consecutiveLoginFailures = consecutiveLoginFailures;
	}

	/**
	 * Gets the timestamp when the user account expires. Login will not be allowed afterward.
	 *
	 * @return the timestamp when the user account expires
	 */
	public Date getAccountExpirationDate() {
		return accountExpirationDate;
	}

	/**
	 * Gets a string representation of the day when the user account expires.
	 *
	 * @return stringified account expiration date
	 */
	public String getAccountExpirationDateView() {
		return timestampToDateString(accountExpirationDate);
	}

	/**
	 * Sets the timestamp when the user account expires. Login will not be allowed afterward.
	 *
	 * @param accountExpirationDate
	 *            account expiration date
	 */
	public void setAccountExpirationDate(Date accountExpirationDate) {
		this.accountExpirationDate = accountExpirationDate;
	}

	/**
	 * Gets the timestamp when the user's credentials expire and must be reset.
	 *
	 * @return timestamp when credentials expire
	 */
	public Date getCredentialsExpirationDate() {
		return credentialsExpirationDate;
	}

	/**
	 * Gets a string representation of the day when the user's credentials expire.
	 *
	 * @return stringified credential expiration date
	 */
	public String getCredentialsExpirationDateView() {
		return timestampToDateString(credentialsExpirationDate);
	}

	/**
	 * Sets the timestamp when the user's credentials expire and must be reset.
	 *
	 * @param credentialsExpirationDate
	 *            timestamp when credentials expire
	 */
	public void setCredentialsExpirationDate(Date credentialsExpirationDate) {
		this.credentialsExpirationDate = credentialsExpirationDate;
	}

	/**
	 * Gets the roles assigned to the user.
	 *
	 * @return list of roles
	 */
	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	/**
	 * Sets the roles assigned to the user.
	 *
	 * @param userRoles
	 *            list of roles
	 */
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	/**
	 * Gets the method used to authenticate the account.
	 *
	 * @see AuthenticationMethod
	 * @return method used to authenticate the account
	 */
	public AuthenticationMethod getAuthenticationMethod() {
		return this.authenticationMethod;
	}

	/**
	 * Sets the method used to authenticate the account.
	 *
	 * @see AuthenticationMethod
	 * @param authenticationMethod
	 *            method used to authenticate the account
	 */
	public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
		this.authenticationMethod = authenticationMethod;
	}

	/**
	 * Gets whether the account is authenticated using LDAP.
	 *
	 * @see #isLdapUser()
	 * @see #getAuthenticationMethod()
	 * @return true if the authentication method is {@link AuthenticationMethod#LDAP}, false if not
	 */
	public boolean getLdapUser() {
		return AuthenticationMethod.LDAP.equals(authenticationMethod);
	}

	/**
	 * Gets whether the account is authenticated using LDAP.
	 *
	 * @see #getLdapUser()
	 * @see #getAuthenticationMethod()
	 * @return true if the authentication method is {@link AuthenticationMethod#LDAP}, false if not
	 */
	public boolean isLdapUser() {
		return this.getLdapUser();
	}

	/**
	 * Gets whether the account is authenticated using table-based authentication.
	 *
	 * @see #getAuthenticationMethod()
	 * @return true if the authentication method is {@link AuthenticationMethod#TABLE_BASED}, false if not
	 */
	public boolean isTableBasedUser() {
		return AuthenticationMethod.TABLE_BASED.equals(authenticationMethod);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", enabled=" + enabled
				+ ", accountLocked=" + accountLocked + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", institution=" + institution + ", email=" + email + ", consecutiveLoginFailures="
				+ consecutiveLoginFailures + ", accountExpirationDate=" + accountExpirationDate
				+ ", credentialsExpirationDate=" + credentialsExpirationDate + ", userRoles=" + userRoles
				+ ", authenticationMethod=" + authenticationMethod + "]";
	}

	@Override
	public String getLabel() {
		return firstName + " " + lastName;
	}

}
