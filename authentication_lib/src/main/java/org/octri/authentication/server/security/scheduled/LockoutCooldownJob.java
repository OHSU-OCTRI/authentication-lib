package org.octri.authentication.server.security.scheduled;

import java.time.Instant;
import java.util.Date;

import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.exception.UserManagementException;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.authentication.server.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LockoutCooldownJob {

    private static final Logger log = LoggerFactory.getLogger(LockoutCooldownJob.class);

    private final OctriAuthenticationProperties authenticationProperties;
    private final LoginAttemptService loginAttemptService;
    final private UserService userService;

    /**
     * Constructor.
     * 
     * @param authenticationProperties
     * @param loginAttemptService
     * @param userService
     */
    public LockoutCooldownJob(OctriAuthenticationProperties authenticationProperties,
            LoginAttemptService loginAttemptService, UserService userService) {
        this.authenticationProperties = authenticationProperties;
        this.loginAttemptService = loginAttemptService;
        this.userService = userService;
    }

    /**
     * Scheduled task to periodically unlock accounts that have exceeded the maximum number of login attempts.
     * 
     * Leverages {@link UserService} to poll the database for locked accounts, and checks the most recent failure from
     * {@link LoginAttemptService} to unlock the account if the configured cooldown period has elapsed.
     * 
     * If octri.authentication.lockout-cooldown-period is configured as null, accounts are left locked.
     */
    @Scheduled(cron = "${octri.authentication.lockout-polling-schedule:0 */1 * * * *}")
    public void checkLockoutCooldown() {
        log.info("Running checkLockoutCooldown job");
        var cooldownDuration = authenticationProperties.getLockoutCooldownDuration();
        if (cooldownDuration == null) {
            return;
        }

        var cooldownThreshold = Date.from(Instant.now().minus(cooldownDuration));
        userService.getUnlockableAccounts().stream().forEach(user -> {
            var lastFailure = loginAttemptService.findLastFailure(user.getUsername());
            if (lastFailure == null) {
                log.warn("No failed login attempts found for " + user.getUsername() + "'s locked account");
            } else if (lastFailure.getAttemptedAt().before(cooldownThreshold)) {
                user.setAccountLocked(false);
                try {
                    userService.save(user);
                } catch (UserManagementException e) {
                    log.error("Failed to save unlocked user account", e);
                }
            }
        });
    }
}
