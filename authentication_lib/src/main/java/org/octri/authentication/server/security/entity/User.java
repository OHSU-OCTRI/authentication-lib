package org.octri.authentication.server.security.entity;

import static org.octri.authentication.utils.ValidationUtils.VALID_EMAIL_REGEX;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.octri.authentication.server.security.service.UserService;
import org.octri.authentication.server.view.Labelled;
import org.octri.authentication.validation.Emailable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
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

	public User() {
		super();
		setDefaults();
	}

	/**
	 * Required fields constructor. Defaults are set for other fields - same as
	 * default constructor.
	 *
	 * @deprecated The no-argument constructor and property setters should be used instead.
	 * @param username
	 * @param firstName
	 * @param lastName
	 * @param institution
	 * @param email
	 */
	@Deprecated(forRemoval = true, since = "1.3.0")
	public User(String username, String firstName, String lastName, String institution, String email) {
		super();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.institution = institution;
		this.email = email;
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

	@Column(unique = true)
	@NotNull(message = "Username is required")
	@Size(max = 320, min = 1, message = "Username must be 1-320 characters")
	private String username;

	private String password;

	@NotNull(message = "Enabled must be Yes or No")
	private Boolean enabled;

	@NotNull(message = "Account locked must be Yes or No")
	private Boolean accountLocked;

	@NotNull(message = "First name is required")
	@Size(max = 50, min = 1, message = "First name must be 1-50 characters")
	protected String firstName;

	@NotNull(message = "Last name is required")
	@Size(max = 50, min = 1, message = "Last name must be 1-50 characters")
	protected String lastName;

	@Size(max = 100, min = 1, message = "Institution must be 1-50 characters")
	private String institution;

	/**
	 * We can add additional email constraints if required.
	 *
	 * @see http://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-builtin-constraints
	 */
	@Email(message = INVALID_EMAIL_MESSAGE, regexp = VALID_EMAIL_REGEX, groups = Emailable.class)
	@NotNull(message = "Email is required", groups = Emailable.class)
	@Size(max = 320, min = 1, message = "Email must be between 1 and 320 characters", groups = Emailable.class)
	private String email;

	private Integer consecutiveLoginFailures;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date accountExpirationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date credentialsExpirationDate;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_user_role", joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_role", referencedColumnName = "id"))
	private List<UserRole> userRoles;

	public boolean isEnabled() {
		if (this.enabled != null && this.enabled) {
			return true;
		}
		return false;
	}

	@Transient
	private boolean ldapUser;

	/**
	 * NOTE the type has to match isEnabled which is considered a "getter" by the java bean world
	 * In other words Springs BeanWrapper will not find this setter if the types don't match
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountLocked() {
		if (accountLocked == null) {
			return false;
		}
		return accountLocked;
	}

	/**
	 * This is only incremented by the ChimeraAuthenticationProvider on a BadCredentials exception.
	 *
	 * @return
	 */
	public Integer getConsecutiveLoginFailures() {
		if (consecutiveLoginFailures == null) {
			return 0;
		}
		return consecutiveLoginFailures;
	}

	// Used for Spring security accountNonLocked
	public Boolean isAccountNonLocked() {
		return getAccountNonLocked();
	}

	public Boolean getAccountNonLocked() {
		return !getAccountLocked();
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		Assert.notNull(accountNonLocked, "accountNonLocked may not be null");
		setAccountLocked(!accountNonLocked);
	}

	// Spring Security accountNonExpired
	/**
	 * Checks the user accountExpired boolean as well as the accountExpirationDate
	 *
	 * @return
	 */
	public Boolean isAccountNonExpired() {
		boolean accountExpired = getAccountExpired()
				|| (getAccountExpirationDate() != null && getAccountExpirationDate().compareTo(new Date()) <= 0);
		return !accountExpired;
	}

	public Boolean getAccountNonExpired() {
		return !getAccountExpired();
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		Assert.notNull(accountNonExpired, "accountNonExpired may not be null");
		setAccountNonExpired(!accountNonExpired);
	}

	// Spring Security credentialsNonExpired

	/**
	 * @return
	 */
	public Boolean isCredentialsNonExpired() {
		return !isCredentialsExpired();
	}

	/**
	 * TODO: Not sure if this is needed - LDAP only auth
	 *
	 * @return
	 */
	public Boolean isCredentialsExpired() {
		return getCredentialsExpired()
				|| (getCredentialsExpirationDate() != null
						&& getCredentialsExpirationDate().compareTo(new Date()) <= 0);
	}

	public Boolean getCredentialsNonExpired() {
		return !getCredentialsExpired();
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		setCredentialsNonExpired(!credentialsNonExpired);
	}

	/**
	 * Method use to convert a {@link Date} into a date string of format "MM/dd/yyyy". This is used
	 * where you need to display a date in the UI for Hibernate fields of type timestamp.
	 *
	 * @param timestamp
	 * @return Returns a string representation of Date, or empty string if timestamp is null.
	 */
	private String timestampToDateString(Date timestamp) {
		if (timestamp == null) {
			return "";
		}
		return Instant.ofEpochMilli(timestamp.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
				.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
	}

	///////////////////////////
	// Getters and setters
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public Boolean getAccountExpired() {
		return accountExpirationDate != null && accountExpirationDate.before(Date.from(Instant.now()));
	}

	public Boolean getCredentialsExpired() {
		return credentialsExpirationDate != null && credentialsExpirationDate.before(Date.from(Instant.now()));
	}

	public void setConsecutiveLoginFailures(Integer consecutiveLoginFailures) {
		this.consecutiveLoginFailures = consecutiveLoginFailures;
	}

	public Date getAccountExpirationDate() {
		return accountExpirationDate;
	}

	public String getAccountExpirationDateView() {
		return timestampToDateString(accountExpirationDate);
	}

	public void setAccountExpirationDate(Date accountExpirationDate) {
		this.accountExpirationDate = accountExpirationDate;
	}

	public Date getCredentialsExpirationDate() {
		return credentialsExpirationDate;
	}

	public String getCredentialsExpirationDateView() {
		return timestampToDateString(credentialsExpirationDate);
	}

	public void setCredentialsExpirationDate(Date credentialsExpirationDate) {
		this.credentialsExpirationDate = credentialsExpirationDate;
	}

	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	/**
	 * This is a transient property. It is only accurate at the time the user form is submitted and should
	 * not be used for any other logic. A better option is {@link UserService#isLdapUser(User)}.
	 *
	 * @return whether the checkbox in the form indicates that the user is LDAP
	 */
	public boolean getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(boolean ldapUser) {
		this.ldapUser = ldapUser;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", enabled=" + enabled + ", accountLocked=" + accountLocked
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", institution=" + institution + ", email="
				+ email + ", consecutiveLoginFailures=" + consecutiveLoginFailures + ", accountExpirationDate="
				+ accountExpirationDate + ", credentialsExpirationDate=" + credentialsExpirationDate + ", userRoles="
				+ userRoles + ", ldapUser=" + ldapUser + ", id=" + id + "]";
	}

	@Override
	public String getLabel() {
		return firstName + " " + lastName;
	}

}
