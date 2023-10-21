package org.octri.authentication.server.security.entity;

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
		LOGIN, LOGOUT, IMPERSONATION
	}

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
