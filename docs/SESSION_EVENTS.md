# Session Event Recording

To comply with OHSU security policy and facilitate analysis of how long study participants spend on our sites, the OCTRI AuthLib records various authentication and session management events to the database. These include:

* Login attempts (both failed and successful)
* When authenticated user sessions begin
* When authenticated user sessions end (either through user action or timeout)

This file documents how these events are captured and stored.

## Login Attempts

Login attempts are captured and persisted to the `login_attempt` table. Successful logins are recorded by [`ApplicationAuthenticationSuccessHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationSuccessHandler.java), while unsuccessful logins are recorded by [`ApplicationAuthenticationFailureHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationFailureHandler.java).

### Classes

* [`LoginAttempt`](../authentication_lib/src/main/java/org/octri/authentication/server/security/entity/LoginAttempt.java) - entity representing entries in the `login_attempt` table
* [`LoginAttemptRepository`](../authentication_lib/src/main/java/org/octri/authentication/server/security/repository/LoginAttemptRepository.java) - JPA repository interface used to work with `LoginAttempt` entities
* [`LoginAttemptService`](../authentication_lib/src/main/java/org/octri/authentication/server/security/service/LoginAttemptService.java) - service wrapper for `LoginAttemptRepository`
* [`AuditLoginAuthenticationSuccessHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/AuditLoginAuthenticationSuccessHandler.java) - abstract base class for authentication success handler; encapsulates logic to create `LoginAttempt` entities for successful logins and reset `User` entity's failed login count
* [`ApplicationAuthenticationSuccessHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationSuccessHandler.java) - concrete authentication success handler
* [`AuditLoginAuthenticationFailureHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/AuditLoginAuthenticationFailureHandler.java) - abstract base class for authentication failure handler; encapsulates logic to create `LoginAttempt` entities for failed logins and update `User` metadata related to failed logins (failure count, account lock)
* [`ApplicationAuthenticationFailureHandler`](../authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationFailureHandler.java) - concrete authentication failure handler

## Session Events

The times when user sessions begin and end are persisted to the `session_event` table to facilitate calculating session durations (the total time the user was logged in). Session initiation is recorded by [`ApplicationAuthenticationSuccessHandler`](./authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationSuccessHandler.java), while session termination is recorded by [`SessionDestroyedListener`](./authentication_lib/src/main/java/org/octri/authentication/server/security/SessionDestroyedListener.java), whether the session is terminated by user logout or session timeout due to inactivity.

### Architecture

To allow calculating how long a user spends on the site, we record an entry in the `session_event` table when the user logs in (a `LOGIN` event), and we record a corresponding entry in the `session_event` table when their session ends (a `LOGOUT` event). The session ID and user ID for these entries should match.

Spring Security delivers events when sessions are created, when session IDs are changed, and when sessions are destroyed. However, session event recording cannot be implemented exclusively using event listeners, because session creations and ID changes happen when the security context is not populated with an authenticated user. Thus the start of the user session must be logged by the [`ApplicationAuthenticationSuccessHandler`](./authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationSuccessHandler.java), because no user is authenticated yet when the session is created or when the session ID is updated.

To make this explicit, the following list shows the session events in sequence, the code executed, and the state of the security context.

1. The user submits the login form. At this point, the security context is populated with an anonymous login token.
2. If authentication is successful, Spring Security changes the session ID to mitigate session fixation attacks.
3. The [`HttpSessionEventPublisher`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/session/HttpSessionEventPublisher.html) publishes an [`HttpSessionIdChangedEvent`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/session/HttpSessionIdChangedEvent.html).
4. Any listener that handles the session ID change is executed. The security context is unpopulated at this point (the `Authentication` is null).
5. The authentication success handler is executed. By this time, the security context has been populated with an `Authentication` representing the user.
6. If the user actively logs out or their session times out automatically, the `HttpSessionEventPublisher` publishes an [`HttpSessionDestroyedEvent`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/session/HttpSessionDestroyedEvent.html).
7. Any listener that handles the session destruction is executed. The security context still contains an `Authentication` representing the user.
8. A new session is created.
9. The `HttpSessionEventPublisher` publishes an [`HttpSessionCreatedEvent`](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/web/session/HttpSessionCreatedEvent.html). The security context is populated with an anonymous login token.

The `LOGIN` event is recorded by the authentication success handler, because this is the first time during session initiation when both the final session ID and the authenticated user are available.

One caveat to this approach is that session event logging can break down if the application is restarted while users are logged in. In this scenario, the restart will prevent the `LOGOUT` event from being captured, leaving the impression that the session never ended.

### Classes

* [`SessionEvent`](./authentication_lib/src/main/java/org/octri/authentication/server/security/entity/SessionEvent.java) - entity representing entries in the `session_event` table
* [`SessionEventRepository`](./authentication_lib/src/main/java/org/octri/authentication/server/security/repository/SessionEventRepository.java) - JPA repository interface used to work with `SessionEvent` entities
* [`SessionEventService`](./authentication_lib/src/main/java/org/octri/authentication/server/security/service/SessionEventService.java) - service wrapper for `SessionEventRepository` and business logic for recording session events
* [`ApplicationAuthenticationSuccessHandler`](./authentication_lib/src/main/java/org/octri/authentication/server/security/ApplicationAuthenticationSuccessHandler.java) - concrete authentication success handler; records the initiation of the user's session after the final ID is assigned and the security context is populated
* [`SessionDestroyedListener`](./authentication_lib/src/main/java/org/octri/authentication/server/security/SessionDestroyedListener.java) - listens for session destruction events and records the end of the session

