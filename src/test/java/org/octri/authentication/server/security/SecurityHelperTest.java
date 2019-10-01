package org.octri.authentication.server.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.octri.authentication.server.security.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityHelperTest {

	private User user;

	private SecurityHelper securityHelper;

	@Before
	public void setUp() {
		user = new User();
		securityHelper = new SecurityHelper(SecurityContextHolder.getContext());
	}

	@Test
	public void testIsLdapUser() {
		user.setEmail(null);
		assertFalse("Should be false with NULL email", securityHelper.isLdapUser(user));

		user.setEmail("");
		assertFalse("Should be false with empty string", securityHelper.isLdapUser(user));

		user.setEmail("  \n  \t  ");
		assertFalse("Should be false with only whitespace", securityHelper.isLdapUser(user));

		user.setEmail("foo@example.com");
		assertFalse("Should be false unless using an email address with ohsu.edu domain", securityHelper.isLdapUser(user));

		user.setEmail("foo@ohsu.edu");
		assertTrue("Should be true when using an email address with ohsu.edu domain", securityHelper.isLdapUser(user));
	}
}