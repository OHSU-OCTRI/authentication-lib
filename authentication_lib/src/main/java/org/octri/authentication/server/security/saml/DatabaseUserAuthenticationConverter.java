package org.octri.authentication.server.security.saml;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.service.UserService;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.util.CollectionUtils;

/**
 * A custom authentication response converter that looks up user details from the database using the username extracted
 * from the SAML response.
 */
public class DatabaseUserAuthenticationConverter implements Converter<ResponseToken, Saml2Authentication> {

	private static final Log log = LogFactory.getLog(DatabaseUserAuthenticationConverter.class);

	private SamlProperties samlProperties;
	private UserService userService;

	public DatabaseUserAuthenticationConverter(UserService userService, SamlProperties samlProperties) {
		this.samlProperties = samlProperties;
		this.userService = userService;
	}

	@Override
	public Saml2Authentication convert(ResponseToken responseToken) {
		Response response = responseToken.getResponse();
		Saml2AuthenticationToken token = responseToken.getToken();
		Assertion assertion = CollectionUtils.firstElement(response.getAssertions());
		Map<String, List<Object>> attributes = AssertionUtils.getAssertionAttributes(assertion);

		String username = (String) CollectionUtils
				.firstElement(attributes.get(samlProperties.getUseridAttribute()));
		log.debug("Username extracted from assertion: " + username);

		User user = userService.findByUsername(username);
		log.debug("User: " + user);

		List<UserRole> userRoles = user.getUserRoles();
		List<GrantedAuthority> authorities = userRoles.stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleName()))
				.collect(Collectors.toList());
		log.debug("User roles: " + authorities);

		NameID nameId = assertion.getSubject().getNameID();
		ApplicationSaml2AuthenticatedPrincipal principal = new ApplicationSaml2AuthenticatedPrincipal(user, authorities,
				nameId,
				attributes);
		principal.setRelyingPartyRegistrationId(token.getRelyingPartyRegistration().getRegistrationId());

		log.debug("Logging in SAML2 principal: " + principal);
		return new Saml2Authentication(principal, token.getSaml2Response(), authorities);
	}

}
