package org.octri.authentication.server.security;

import org.octri.authentication.server.security.entity.SessionEvent.EventType;
import org.octri.authentication.server.security.service.SessionEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

/**
 * Listener that logs session destroyed events. Triggered by both user-initiated logout and session timeout.
 */
@Component
public class SessionDestroyedListener implements ApplicationListener<SessionDestroyedEvent> {

	@Autowired
	private SessionEventService sessionEventService;

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		sessionEventService.logEvent(EventType.LOGOUT, event.getId(), null);
	}

}
