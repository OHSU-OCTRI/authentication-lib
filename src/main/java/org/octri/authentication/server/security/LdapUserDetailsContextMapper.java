/**
 * 
 */
package org.octri.authentication.server.security;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.util.Assert;

/**
 * An implementation of Spring Security's {@link UserDetailsContextMapper}, to generate {@link UserDetails} from an LDAP
 * context.
 * 
 * We basically just use this to ensure that a valid user and password were in LDAP then create a
 * {@link UserDetails} using information from the database
 * 
 * Implementation essentially lifted from Chimera.
 *
 * @author harrelst
 *
 */
public class LdapUserDetailsContextMapper implements UserDetailsContextMapper {

	private static final Log log = LogFactory.getLog(LdapUserDetailsContextMapper.class);

	private UserDetailsService userDetailsService;

	public LdapUserDetailsContextMapper(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserFromContext(org.springframework.
	 * ldap.core.DirContextOperations, java.lang.String, java.util.Collection)
	 */
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		doPreAuthenticationChecks(userDetails);
		return userDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserToContext(org.springframework.
	 * security.core.userdetails.UserDetails, org.springframework.ldap.core.DirContextAdapter)
	 */
	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		throw new RuntimeException("Mapping to LDAP context not supported.");
	}

	/**
	 * Check to determine if user's account has been suspended locally. Conceptually this doesn't seem like the best
	 * place for these checks, but the LdapAuthenticationProvider never does them and waiting until after we have the
	 * UserDetails object is consistent with how the DaoAuthenticationProvider works.
	 */
	protected void doPreAuthenticationChecks(UserDetails user) {
		Assert.notNull(user, "user may not be null");

		// Should we check for locked accounts here?
		if (!user.isAccountNonLocked()) {
			log.debug("User account is locked");
			throw new LockedException("User account is locked");
		}

		if (!user.isEnabled()) {
			log.debug("User account is disabled");
			throw new DisabledException("User is disabled");
		}

		if (!user.isAccountNonExpired()) {
			log.debug("User account is expired");
			throw new AccountExpiredException("User account has expired");
		}
	}

}
