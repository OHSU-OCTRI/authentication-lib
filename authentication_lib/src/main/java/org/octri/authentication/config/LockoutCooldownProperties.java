package org.octri.authentication.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

/**
 * Configuration properties to control automatic unlock of user accounts after cooldown period.
 */
@ConfigurationProperties(prefix = "octri.authentication.lockout-cooldown")
public class LockoutCooldownProperties {

    /**
     * The default value of "octri.authentication.lockout-cooldown.duration"
     */
    public static final Duration DEFAULT_COOLDOWN_DURATION = Duration.ofMinutes(30);

    /**
     * Feature flag to control whether user accounts are automatically unlocked after a cooldown period.
     */
    private Boolean enabled = false;

    /**
     * Minimum time that must elapse between the most recent failed login and automatic account unlock.
     * Defaults to 30 minutes.
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration duration = DEFAULT_COOLDOWN_DURATION;

    /**
     * Schedule for cron task to check cooldown on locked accounts. Defaults to every minute.
     */
    private String pollingSchedule = "0 */1 * * * *";

    /**
     * Set the feature flag that controls feature for automatic account unlock
     * 
     * @return
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Get the feature flag that controls feature for automatic account unlock
     * 
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the cooldown period for user lockouts due to failed login attempts
     * 
     * @return
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set the cooldown period for user lockouts due to failed login attempts
     * 
     * @param duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Gets the cron schedule for checking the database for locked users
     * 
     * @return
     */
    public String getPollingSchedule() {
        return pollingSchedule;
    }

    /**
     * Sets the cron schedule for checking the database for locked users
     * 
     * @param pollingSchedule
     */
    public void setPollingSchedule(String pollingSchedule) {
        this.pollingSchedule = pollingSchedule;
    }

}
