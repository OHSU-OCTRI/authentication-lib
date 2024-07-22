package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the authentication library.
 */
@ConfigurationProperties(prefix = "octri.authentication")
public class OctriRegistrationProperties {

    /**
     * Default value for tokenValidDuration
     */
    private Duration tokenValidDuration = Duration.parse("PT30M");
    
    public Duration getTokenValidDuration() {
        return this.tokenValidDuration;
    }

    public void setTokenValidDuration(String overrideDuration) {
        this.tokenValidDuration = Duration.parse(overrideDuration);
    }


	@Override
	public String toString() {
		return "OctriConfigurationProperties [tokenValidationDuration=" + tokenValidDuration + "]";
	}
}
