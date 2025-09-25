package org.octri.authentication.server.security.entity;

import org.octri.common.domain.AbstractEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

/**
 * Entity for logging session events.
 */
@Entity
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
