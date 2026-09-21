package org.octri.authentication.server.security.scheduled;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.LockoutCooldownProperties;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.UserManagementException;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.authentication.server.security.service.UserService;

@ExtendWith(MockitoExtension.class)
public class LockoutCooldownJobTest {

	private static final String USERNAME = "foo";
	private static final Duration COOLDOWN_DURATION = Duration.ofMinutes(30);

	@Mock
	private LockoutCooldownProperties lockoutCooldownProperties;

	@Mock
	private LoginAttemptService loginAttemptService;

	@Mock
	private UserService userService;

	private LockoutCooldownJob lockoutCooldownJob;

	private User user;

	@BeforeEach
	public void beforeEach() {
		lockoutCooldownJob = new LockoutCooldownJob(lockoutCooldownProperties, loginAttemptService, userService);

		user = new User();
		user.setUsername(USERNAME);
		user.setAccountLocked(true);
	}

	@Test
	public void testCooldownHasElapsed() throws UserManagementException {
		when(lockoutCooldownProperties.getDuration()).thenReturn(COOLDOWN_DURATION);
		when(userService.getUnlockableAccounts()).thenReturn(List.of(user));

		var lastFailure = new LoginAttempt();
		lastFailure.setAttemptedAt(Date.from(Instant.now().minus(COOLDOWN_DURATION).minusSeconds(60)));
		when(loginAttemptService.findLastFailure(USERNAME)).thenReturn(lastFailure);

		lockoutCooldownJob.checkLockoutCooldown();

		assertFalse(user.getAccountLocked(), "Account should be unlocked once the cooldown period has elapsed");
		verify(userService, times(1)).save(user);
	}

	@Test
	public void testCooldownHasNotElapsed() throws UserManagementException {
		when(lockoutCooldownProperties.getDuration()).thenReturn(COOLDOWN_DURATION);
		when(userService.getUnlockableAccounts()).thenReturn(List.of(user));

		var lastFailure = new LoginAttempt();
		lastFailure.setAttemptedAt(Date.from(Instant.now().minus(COOLDOWN_DURATION).plusSeconds(60)));
		when(loginAttemptService.findLastFailure(USERNAME)).thenReturn(lastFailure);

		lockoutCooldownJob.checkLockoutCooldown();

		assertTrue(user.getAccountLocked(), "Account should remain locked until the cooldown period has elapsed");
		verify(userService, never()).save(user);
	}

	@Test
	public void testNoFailedLoginAttemptIsFound() throws UserManagementException {
		when(lockoutCooldownProperties.getDuration()).thenReturn(COOLDOWN_DURATION);
		when(userService.getUnlockableAccounts()).thenReturn(List.of(user));
		when(loginAttemptService.findLastFailure(USERNAME)).thenReturn(null);

		lockoutCooldownJob.checkLockoutCooldown();

		assertTrue(user.getAccountLocked(),
				"Account should remain locked when there is no failed login attempt to compare against");
		verify(userService, never()).save(user);
	}

	@Test
	public void testFailureToSaveIsHandledGracefully() throws UserManagementException {
		when(lockoutCooldownProperties.getDuration()).thenReturn(COOLDOWN_DURATION);
		when(userService.getUnlockableAccounts()).thenReturn(List.of(user));

		var lastFailure = new LoginAttempt();
		lastFailure.setAttemptedAt(Date.from(Instant.now().minus(COOLDOWN_DURATION).minusSeconds(60)));
		when(loginAttemptService.findLastFailure(USERNAME)).thenReturn(lastFailure);
		when(userService.save(user)).thenThrow(new UserManagementException("Could not save user"));

		assertDoesNotThrow(() -> lockoutCooldownJob.checkLockoutCooldown(),
				"UserManagementException from a failed save should be caught, not propagated");

		verify(userService, times(1)).save(user);
	}
}
