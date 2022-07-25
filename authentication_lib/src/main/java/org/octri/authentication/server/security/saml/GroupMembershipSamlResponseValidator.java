package org.octri.authentication.server.security.saml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.SamlProperties;
import org.opensaml.saml.saml2.core.Assertion;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.core.Saml2ErrorCodes;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.AssertionToken;
import org.springframework.util.CollectionUtils;

/**
 * Creates a custom SAML assertion validator that verifies that the user is a member of the expected group. Group
 * membership information is extracted from the assertions included in the SAML response, and if the expected group is
 * found, authentication is allowed. Otherwise, authentication is denied.
 */
public class GroupMembershipSamlResponseValidator implements Converter<AssertionToken, Saml2ResponseValidatorResult> {

	private static final Log log = LogFactory.getLog(GroupMembershipSamlResponseValidator.class);

	private SamlProperties samlProperties;

	public GroupMembershipSamlResponseValidator(SamlProperties samlProperties) {
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
		List<String> groups = getGroups(attributeMap);
		log.debug("Required group: " + samlProperties.getRequiredGroup());
		log.debug("Groups extracted from assertion: " + groups);

		if (!groups.contains(samlProperties.getRequiredGroup())) {
			String message = username + " is not a member of " + samlProperties.getRequiredGroup();
			log.debug(message);
			return result.concat(new Saml2Error(Saml2ErrorCodes.INVALID_ASSERTION, message));
		}

		return result;
	}

	/**
	 * Extracts the user's group information from the SAML assertion attributes.
	 *
	 * @param assertionAttributes
	 * @return a possibly empty list of group names
	 */
	private List<String> getGroups(Map<String, List<Object>> assertionAttributes) {
		String attributeName = samlProperties.getGroupAttribute();
		List<Object> assertionGroups = assertionAttributes.get(attributeName);

		if (assertionGroups == null) {
			log.warn("Group attribute " + attributeName + " was not found in the SAML response. "
					+ "This may indicate a configuration error.");
			return new ArrayList<String>();
		}

		return assertionGroups.stream()
				.map(g -> g.toString())
				.collect(Collectors.toList());
	}

}
