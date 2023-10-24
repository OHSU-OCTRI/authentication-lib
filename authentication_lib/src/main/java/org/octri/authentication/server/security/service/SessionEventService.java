package org.octri.authentication.server.security.service;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.SecurityHelper;
import org.octri.authentication.server.security.entity.SessionEvent;
import org.octri.authentication.server.security.entity.SessionEvent.EventType;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.SessionEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Service wrapper for {@link SessionEventRepository}.
 */
@Service
public class SessionEventService {

	private static final Log log = LogFactory.getLog(SessionEventService.class);

	@Autowired
	private SessionEventRepository sessionEventRepository;

	@Autowired
	private UserService userService;

	/**
	 * Log {@link SessionEvent} for the currently authenticated user. Event saved to database. Session id acquired from
	 * {@link RequestContextHolder}. Use {@link #logEvent(EventType, String)} to manually specify the session id.
	 *
	 * @param event
	 */
	public void logEvent(final EventType event) {
		final RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Assert.notNull(attrs, "Could not find request attributes");
		String sessionId = attrs.getSessionId();
		Assert.notNull(sessionId, "Could not find a session id");
		User asUser = null;
		if (event.equals(EventType.IMPERSONATION)) {
			SecurityHelper securityHelper = new SecurityHelper(SecurityContextHolder.getContext());
			asUser = userService.findByUsername(securityHelper.username());
		}
		logEvent(event, sessionId, asUser);
	}

	/**
	 * Log {@link SessionEvent} for the currently authenticated user. Event saved to database.
	 *
	 * @param event
	 * @param sessionId
	 * @param asUser
	 *            If this is an impersonation event, pass the user being impersonated
	 */
	public void logEvent(final EventType event, final String sessionId, final User asUser) {
		log.debug("Logging " + event.toString() + " for session id " + sessionId + ".");
		Assert.notNull(sessionId, "Must provide a session id");
		final SecurityHelper helper = new SecurityHelper(SecurityContextHolder.getContext());

		final Optional<SessionEvent> login = findLoginEvent(sessionId);
		switch (event) {
			case LOGIN:
				if (helper.isLoggedIn() && !login.isPresent()) {
					sessionEventRepository.save(new SessionEvent(sessionId, event, findUser(), null));
				}
				break;
			case LOGOUT:
				final Optional<SessionEvent> logout = findLogoutEvent(sessionId);
				if (login.isPresent() && !logout.isPresent()) {
					sessionEventRepository.save(new SessionEvent(sessionId, event, login.get().getUser(), null));
				}
				break;
			case IMPERSONATION:
				if (login.isPresent()) {
					sessionEventRepository.save(new SessionEvent(sessionId, event, login.get().getUser(), asUser));
				} else {
					// If for some reason the session can't be found, we should still log the impersonation!
					log.error("A user is being impersonated, but the original login session was not recorded!");
					sessionEventRepository.save(new SessionEvent(sessionId, event, asUser, asUser));
				}
				break;
			default:
				throw new IllegalArgumentException(
						"Unsupported EventType - not saving event " + event + " for session id " + sessionId);
		}
	}

	/**
	 * Find {@link EventType#LOGIN} event for given sessionId if one exists
	 *
	 * @param sessionId
	 * @return Login event for session id.
	 */
	public Optional<SessionEvent> findLoginEvent(final String sessionId) {
		return sessionEventRepository.findFirstBySessionIdAndEvent(sessionId, EventType.LOGIN);
	}

	/**
	 * Find {@link EventType#LOGOUT} event for given sessionId if one exists.
	 *
	 * @param sessionId
	 * @return Logout event for session id.
	 */
	public Optional<SessionEvent> findLogoutEvent(final String sessionId) {
		return sessionEventRepository.findFirstBySessionIdAndEvent(sessionId, EventType.LOGOUT);
	}

	/**
	 * Find authenticated {@link User}, otherwise throw exception.
	 *
	 * @return
	 */
	private User findUser() {
		SecurityHelper helper = new SecurityHelper(SecurityContextHolder.getContext());
		User user = userService.find(helper.authenticationUserDetails().getUserId());
		Assert.notNull(user, "Could not find an authenticated user for logging a session event.");
		return user;
	}

}