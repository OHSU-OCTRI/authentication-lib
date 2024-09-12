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
 * and an expiration date for the request. A token is generated automatically when constructing a new
 * {@link PasswordResetToken} using {@link #PasswordResetToken(User)} as well as an expiry date.
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
	 *
	 * @return whether the token expiration date has passed
	 */
	public boolean isExpired() {
		Date now = new Date();
		return now.after(getExpiryDate());
	}

}