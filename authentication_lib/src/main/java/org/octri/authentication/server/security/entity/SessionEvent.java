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
		/**
		 * Successful user login
		 */
		LOGIN,

		/**
		 * Logout due to user action or session timeout
		 */
		LOGOUT,

		/**
		 * Admin account impersonating a regular user
		 */
		IMPERSONATION
	}

	/**
	 * Optimistic locking version.
	 */
	@Version
	protected Integer version;

	/**
	 * Timestamp when the record was created.
	 */
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	protected Date createdAt;

	/**
	 * Timestamp when the record was last updated.
	 */
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updatedAt;

	/**
	 * ID of the user session that triggered the event.
	 */
	@NotNull
	private String sessionId;

	/**
	 * The type of event being recorded.
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
	private EventType event;

	/**
	 * The user account that triggered the event.
	 */
	@NotNull
	@ManyToOne
	private User user;

	/**
	 * The user account being impersonated.
	 */
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
	 * @param sessionId
	 *            the session ID
	 * @param eventType
	 *            the type of event being recorded
	 * @param user
	 *            the user account that triggered the event
	 * @param asUser
	 *            the user account being impersonated, if event is {@link EventType#IMPERSONATION}
	 */
	public SessionEvent(final String sessionId, final EventType eventType, final User user, final User asUser) {
		this.sessionId = sessionId;
		this.event = eventType;
		this.user = user;
		this.asUser = asUser;
	}

	/**
	 * Gets the optimistic locking version.
	 *
	 * @return optimistic locking version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Sets the optimistic locking version
	 *
	 * @param version
	 *            optimistic locking version
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Gets the timestamp when the event record was created.
	 *
	 * @return when the event record was created
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the timestamp when the event record was created.
	 *
	 * @param createdAt
	 *            when the event record was created
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Gets the timestamp when the event record was last updated.
	 *
	 * @return when the event record was last updated
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Sets the timestamp when the event record was last updated.
	 *
	 * @param updatedAt
	 *            when the event record was last updated
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * Gets the ID of the user session that triggered the event.
	 *
	 * @return the session ID
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the ID of the user session that triggered the event.
	 *
	 * @param sessionId
	 *            the session ID
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Gets the type of event being recorded.
	 *
	 * @see EventType
	 * @return the type of event
	 */
	public EventType getEvent() {
		return event;
	}

	/**
	 * Sets the type of event being recorded.
	 *
	 * @see EventType
	 * @param event
	 *            the type of event
	 */
	public void setEvent(EventType event) {
		this.event = event;
	}

	/**
	 * Gets the user account that triggered the event.
	 *
	 * @return user account that triggered the event
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user account that triggered the event.
	 *
	 * @param user
	 *            user account that triggered the event
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the user being impersonated. Only valid if event is {@link EventType#IMPERSONATION}.
	 *
	 * @return the user account being impersonated
	 */
	public User getAsUser() {
		return asUser;
	}

	/**
	 * Sets the user being impersonated. Only valid if event is {@link EventType#IMPERSONATION}.
	 *
	 * @param asUser
	 *            the user account being impersonated
	 */
	public void setAsUser(User asUser) {
		this.asUser = asUser;
	}

}
