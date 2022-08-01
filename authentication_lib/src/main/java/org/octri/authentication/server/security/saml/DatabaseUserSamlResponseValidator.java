package org.octri.authentication.server.security.saml;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.UserService;
import org.opensaml.saml.saml2.core.Assertion;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.AssertionToken;
import org.springframework.util.CollectionUtils;

/**
 * A custom SAML response validator that verifies that the user is present in the database. The username is
 * extracted from the SAML response, and if an active user account with the given username is found in the database,
 * authentication is allowed. Otherwise authentication is denied.
 */
public class DatabaseUserSamlResponseValidator implements Converter<AssertionToken, Saml2ResponseValidatorResult> {

	private static final Log log = LogFactory.getLog(DatabaseUserSamlResponseValidator.class);

	private UserService userService;
	private SamlProperties samlProperties;

	public DatabaseUserSamlResponseValidator(UserService userService, SamlProperties samlProperties) {
		this.userService = userService;
		this.samlProperties = samlProperties;
	}

	@Override
	public Saml2ResponseValidatorResult convert(AssertionToken assertionToken) {
		Saml2ResponseValidatorResult result = OpenSaml4AuthenticationProvider
				.createDefaultAssertionValidator()
				.convert(assertionToken);

		Assertion assertion = assertionToken.getAssertion();
		Map<String, List<Object>> attributeMap = AssertionUtils.getAssertionAttributes(assertion);
		String username = (String) CollectionUtils
				.firstElement(attributeMap.get(samlProperties.getUseridAttribute()));
		log.debug("Username extracted from assertion: " + username);

		User user = userService.findByUsername(username);
		log.debug("User: " + user);

		if (userMissingOrDisabled(user)) {
			String message = username + " has not been granted access to this application.";
			log.debug(message);
			return result.concat(new Saml2Error(Saml2ErrorCodes.INVALID_ASSERTION, message));
		}

		return result;
	}

	/**
	 * Reports whether the user is missing or had metadata indicating that the user should not be allowed to log in.
	 * Returns true if the user was not found in the database, their account is disabled, their account is locked, or
	 * their account has expired.
	 *
	 * @param user
	 * @return true if the user account is missing, disabled, locked, or expired
	 */
	private boolean userMissingOrDisabled(User user) {
		return user == null || !user.getEnabled() || user.getAccountLocked() || user.getAccountExpired();
	}

}
