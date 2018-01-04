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

	// Used directly in ThymeLeaf templates
	public static final Integer EXPIRE_IN_MINUTES = 30;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	/**
	 * Default contructor, no fields are set.
	 */
	public PasswordResetToken() {
	}

	/**
	 * This constructor sets all required fields based on the passed in {@link User}.
	 * 
	 * @param user
	 */
	public PasswordResetToken(User user) {
		this.token = UUID.randomUUID().toString();
		this.expiryDate = Date.from(Instant.now().plus(PasswordResetToken.EXPIRE_IN_MINUTES, ChronoUnit.MINUTES));
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

}