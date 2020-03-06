package org.octri.authentication.server.security.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Holds a random token (UUID) generated at the time of the request, the {@link User} that requested the password reset,
 * and an expiration date for the request. A token is generated automatically when constructing a new
 * {@link PasswordResetToken} using {@link #PasswordResetToken(User)} as well as an expiry date.
 *
 * @author sams
 */
@Entity
public class PasswordResetToken {

	/**
	 * This property is used to create a {@link PasswordResetToken}. It is also presented in templates.
	 */
	public static final Integer EXPIRE_IN_MINUTES = 30;

	/**
	 * A long expiration period. Intended for use when the 'noemail' profile is active. Admins will manually send the
	 * reset token to users so the expiration period needs to be longer to account for the handoff.
	 */
	public static final Integer LONG_EXPIRE_IN_MINUTES = 20160;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "token", unique = true)
	private String token;

	@NotNull
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user")
	private User user;

	@NotNull
	@Column(name = "expiry_date")
	private Date expiryDate;

	@Transient
	private String tokenUrl;

	/**
	 * Default contructor, no fields are set.
	 */
	public PasswordResetToken() {
	}

	/**
	 * This constructor sets all required fields based on the passed in {@link User}. The default expiration period,
	 * {@link #EXPIRE_IN_MINUTES}, is used.
	 *
	 * @param user
	 */
	public PasswordResetToken(User user) {
		this.token = UUID.randomUUID().toString();
		this.expiryDate = Date.from(Instant.now().plus(PasswordResetToken.EXPIRE_IN_MINUTES, ChronoUnit.MINUTES));
		this.user = user;
	}

	/**
	 * Optionally used by applications to control the expiration date of the password reset token.
	 *
	 * @param user
	 * @param expireInMinutes
	 */
	public PasswordResetToken(User user, int expireInMinutes) {
		this.token = UUID.randomUUID().toString();
		this.expiryDate = Date.from(Instant.now().plus(expireInMinutes, ChronoUnit.MINUTES));
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
	
	/**
	 * This helper method checks whether this token has expired.
	 * @return whether the token expiration date has passed
	 */
	public boolean isExpired() {
		Date now = new Date();
		return now.after(getExpiryDate());

	}

}