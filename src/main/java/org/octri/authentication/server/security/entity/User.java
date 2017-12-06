package org.octri.authentication.server.security.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

/**
 * Represents data related to a user account.
 * 
 * @author yateam
 *
 */
@Entity
public class User extends AbstractEntity {

	public User() {
		super();
		setDefaults();
	}

	/**
	 * Required fields constructor. Defaults are set for other fields - same as
	 * default constructor.
	 * 
	 * @param username
	 * @param firstName
	 * @param lastName
	 * @param institution
	 * @param email
	 */
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
		this.accountExpired = false;
		this.credentialsExpired = true;
		this.consecutiveLoginFailures = 0;
	}

	@Column(unique = true)
	@NotNull
	@Size(max = 50, min = 1)
	private String username;

	@Size(max = 128)
	private String password;

	@NotNull
	private Boolean enabled;

	@NotNull
	private Boolean accountLocked;

	@NotNull
	private Boolean accountExpired;

	@NotNull
	private Boolean credentialsExpired;

	@NotNull
	@Size(max = 50, min = 1)
	protected String firstName;

	@NotNull
	@Size(max = 50, min = 1)
	protected String lastName;

	@NotNull
	@Size(max = 100)
	private String institution;

	@NotNull
	@Size(max = 100, min = 1)
	private String email;

	@NotNull
	private Integer consecutiveLoginFailures;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date accountExpirationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date credentialsExpirationDate;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_user_role", 
               joinColumns = @JoinColumn(name = "user", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "user_role", referencedColumnName = "id"))
	private List<UserRole> userRoles;

	public boolean isEnabled() {
		if (this.enabled != null && this.enabled) {
			return true;
		}
		return false;
	}

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
		return accountExpired;
	}

	public void setAccountExpired(Boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public Boolean getCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(Boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public void setConsecutiveLoginFailures(Integer consecutiveLoginFailures) {
		this.consecutiveLoginFailures = consecutiveLoginFailures;
	}

	public Date getAccountExpirationDate() {
		return accountExpirationDate;
	}

	public void setAccountExpirationDate(Date accountExpirationDate) {
		this.accountExpirationDate = accountExpirationDate;
	}

	public Date getCredentialsExpirationDate() {
		return credentialsExpirationDate;
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

}
