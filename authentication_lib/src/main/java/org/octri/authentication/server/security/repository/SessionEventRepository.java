package org.octri.authentication.server.security.repository;

import java.util.Optional;

import org.octri.authentication.server.security.entity.SessionEvent;
import org.octri.authentication.server.security.entity.SessionEvent.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link JpaRepository} for manipulating {@link SessionEvent} entities.
 */
public interface SessionEventRepository extends JpaRepository<SessionEvent, Long> {

	/**
	 * Finds an event by session ID and event type.
	 * 
	 * @param sessionId
	 *            session ID
	 * @param eventType
	 *            event type
	 * @return a session event if an event of the given type exists for the given session, otherwise empty
	 */
	Optional<SessionEvent> findFirstBySessionIdAndEvent(String sessionId, EventType eventType);

}
