package org.octri.authentication.server.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log log = LogFactory.getLog(SessionDestroyedListener.class);

	@Autowired
	private SessionEventService sessionEventService;

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		log.debug("Session destroyed event: " + event);
		sessionEventService.logEvent(EventType.LOGOUT, event.getId(), null);
	}

}
