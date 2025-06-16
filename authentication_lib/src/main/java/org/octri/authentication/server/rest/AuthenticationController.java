package org.octri.authentication.server.rest;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This {@link RestController} handles authentication related requests.
 *
 * @author yateam
 */
@RestController
public class AuthenticationController {

	/**
	 * @param user
	 *            the current principal
	 * @return Return the authenticated {@link Principal}.
	 */
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

}
