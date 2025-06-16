package org.octri.authentication.server.security.entity;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

/**
 * Holds a random token (UUID) generated at the time of the request, the {@link User} that requested the password reset,
 * and an expiration date for the request.
 *
 * @author sams
 */
@Entity
public class PasswordResetToken {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "token", unique = true)
	private String token = UUID.randomUUID().toString();

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
	 * Gets the record's unique ID.
	 * 
	 * @return the unique ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the record's unique ID.
	 * 
	 * @param id
	 *            the ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the token string.
	 * 
	 * @return the token string
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token string.
	 * 
	 * @param token
	 *            the token string to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Gets the user account that can be reset using the token.
	 * 
	 * @return the associated user account
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user account that can be reset using the token.
	 * 
	 * @param user
	 *            a user account
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the timestamp when the token expires and can no longer be used.
	 * 
	 * @return when the token expires
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Sets the timestamp when the token expires and can no longer be used.
	 * 
	 * @param expiryDate
	 *            when the token should expire
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Gets the URL that initiates a password reset using the token.
	 * 
	 * @return password reset URL
	 */
	public String getTokenUrl() {
		return tokenUrl;
	}

	/**
	 * Sets the URL that initiates a password reset using the token.
	 * 
	 * @param tokenUrl
	 *            password reset URL
	 */
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	/**
	 * This helper method checks whether this token has expired.
	 *
	 * @return whether the token expiration date has passed
	 */
	public boolean isExpired() {
		Date now = new Date();
		return now.after(getExpiryDate());
	}

}