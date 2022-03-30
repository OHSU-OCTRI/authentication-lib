package org.octri.authentication.server.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.octri.authentication.server.security.entity.User;

public class SecurityHelperTest {

	private User user;

	@Before
	public void setUp() {
		user = new User();
	}

	@Test
	public void testIsLdapUser() {
		user.setEmail(null);
		assertFalse("Should be false with NULL email", SecurityHelper.hasOHSUEmail(user));

		user.setEmail("");
		assertFalse("Should be false with empty string", SecurityHelper.hasOHSUEmail(user));

		user.setEmail("  \n  \t  ");
		assertFalse("Should be false with only whitespace", SecurityHelper.hasOHSUEmail(user));

		user.setEmail("foo@example.com");
		assertFalse("Should be false unless using an email address with ohsu.edu domain", SecurityHelper.hasOHSUEmail(user));

		user.setEmail("foo@ohsu.edu");
		assertTrue("Should be true when using an email address with ohsu.edu domain", SecurityHelper.hasOHSUEmail(user));
	}
}