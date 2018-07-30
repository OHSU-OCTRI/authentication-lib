package org.octri.authentication.server.security.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

	// ---- Accessors --------
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getIpAddress() {
		return ipAddress;
	}

	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getAttemptedAt() {
		return attemptedAt;
	}

	public void setAttemptedAt(Date attemptedAt) {
		this.attemptedAt = attemptedAt;
	}

	public Boolean getSuccessful() {
		return successful;
	}

	public void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
