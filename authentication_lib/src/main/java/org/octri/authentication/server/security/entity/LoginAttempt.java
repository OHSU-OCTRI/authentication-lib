package org.octri.authentication.server.security.entity;

import java.util.Date;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 * Used to record successful and failed login attempts for a given username. New records are created in the
 * {@link AuthenticationFailureHandler} and {@link AuthenticationSuccessHandler} objects.
 *
 * Adapted from Chimera's LoginAttempt.
 *
 * @author lawhead
 * @author harrelst
 *
 */
@Configurable
@Entity
public class LoginAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Version
	@Column(name = "version")
	private Integer version;

	@NotNull
	private String username;

	@NotNull
	private String ipAddress;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date attemptedAt;

	@NotNull
	private Boolean successful;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String errorType;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	/**
	 * Gets the record's unique ID.
	 *
	 * @return the unique ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the record's unique ID.
	 *
	 * @param id
	 *            the unique ID
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the record's optimistic locking version.
	 *
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Sets the record's optimistic locking version.
	 *
	 * @param version
	 *            the version
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Gets the username provided in the login request.
	 *
	 * @return the username provided
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username provided in the login request.
	 *
	 * @param username
	 *            the username provided
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the client IP address provided in the login request.
	 *
	 * @return the client IP address
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets the client IP address provided in the login request.
	 *
	 * @param ipAddress
	 *            the client IP address
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the timestamp of the login request.
	 *
	 * @return the request timestamp
	 */
	public Date getAttemptedAt() {
		return attemptedAt;
	}

	/**
	 * Sets the timestamp of the login request.
	 *
	 * @param attemptedAt
	 *            the request timestamp
	 */
	public void setAttemptedAt(Date attemptedAt) {
		this.attemptedAt = attemptedAt;
	}

	/**
	 * Gets whether the user successfully logged in.
	 *
	 * @return true if the attempt was successful, false otherwise
	 */
	public Boolean getSuccessful() {
		return successful;
	}

	/**
	 * Sets whether the user successfully logged in.
	 *
	 * @param successful
	 *            true if the attempt was successful, false otherwise
	 */
	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	/**
	 * Gets the reason that the login attempt failed.
	 *
	 * @return the reason that the login attempt failed
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * Sets the reason that the login attempt failed.
	 *
	 * @param errorType
	 *            the reason that the login attempt failed
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * Gets the optional detailed error message describing why the login attempt failed.
	 *
	 * @return the detailed error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the optional detailed error message describing why the login attempt failed.
	 *
	 * @param errorMessage
	 *            the detailed error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
