package org.octri.authentication.utils;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utility for working with spring profiles. Autowire this component to use.
 */
@Component
public class ProfileUtils {

	public enum AuthProfile {
		noemail;
	}

	@Autowired
	private Environment environment;

	/**
	 * Check if profile is active.
	 *
	 * @param profile
	 * @return
	 */
	public boolean isActive(String profile) {
		return Arrays.stream(environment.getActiveProfiles()).anyMatch(p -> p.equalsIgnoreCase(profile));
	}

	/**
	 * Check if profile is active.
	 *
	 * @param profile
	 * @return
	 */
	public boolean isActive(AuthProfile profile) {
		return isActive(profile.toString());
	}
}
