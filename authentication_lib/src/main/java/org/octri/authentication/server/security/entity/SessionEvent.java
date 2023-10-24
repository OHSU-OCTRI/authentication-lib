package org.octri.authentication.server.security.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 * Entity for logging session events.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SessionEvent extends AbstractEntity {

	/**
	 * Enumeration of event types such as login and logout.
	 */
	public enum EventType {
		LOGIN, LOGOUT, IMPERSONATION
	}

	@Version
	protected Integer version;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	protected Date createdAt;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updatedAt;

	@NotNull
	private String sessionId;

	@NotNull
	@Enumerated(EnumType.STRING)
	private EventType event;

	@NotNull
	@ManyToOne
	private User user;

	@ManyToOne
	private User asUser;

	/**
	 * Default constructor.
	 */
	public SessionEvent() {
	}

	/**
	 * All fields constructor.
	 *
	 * @param eventType
	 * @param user
	 */
	public SessionEvent(final String sessionId, final EventType eventType, final User user, final User asUser) {
		this.sessionId = sessionId;
		this.event = eventType;
		this.user = user;
		this.asUser = asUser;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public EventType getEvent() {
		return event;
	}

	public void setEvent(EventType event) {
		this.event = event;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getAsUser() {
		return asUser;
	}

	public void setAsUser(User asUser) {
		this.asUser = asUser;
	}

}
