package org.octri.authentication.server.security.saml;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.SamlProperties;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.util.CollectionUtils;

/**
 * A custom response authentication converter that extracts user details including the username from the
 * SAML response.
 */
public class SamlResponseUserDetailsAuthenticationConverter implements Converter<ResponseToken, Saml2Authentication> {

    private static final Log log = LogFactory.getLog(SamlResponseUserDetailsAuthenticationConverter.class);

    private SamlProperties samlProperties;

    public SamlResponseUserDetailsAuthenticationConverter(SamlProperties samlProperties) {
        this.samlProperties = samlProperties;
    }

    @Override
    public Saml2Authentication convert(ResponseToken responseToken) {
        Response response = responseToken.getResponse();
        Saml2AuthenticationToken token = responseToken.getToken();
        Assertion assertion = CollectionUtils.firstElement(response.getAssertions());
        Map<String, List<Object>> attributes = AssertionUtils.getAssertionAttributes(assertion);

        NameID nameId = assertion.getSubject().getNameID();
        ApplicationSaml2AuthenticatedPrincipal principal = new ApplicationSaml2AuthenticatedPrincipal(nameId,
                attributes);

        principal.setUsername(AssertionUtils.getAttributeValue(attributes, samlProperties.getUseridAttribute()));
        principal.setFirstName(AssertionUtils.getAttributeValue(attributes, samlProperties.getFirstNameAttribute()));
        principal.setLastName(AssertionUtils.getAttributeValue(attributes, samlProperties.getLastNameAttribute()));
        principal.setEmail(AssertionUtils.getAttributeValue(attributes, samlProperties.getEmailAttribute()));
        principal.setRelyingPartyRegistrationId(token.getRelyingPartyRegistration().getRegistrationId());

        log.debug("Logging in SAML2 principal: " + principal);
        return new Saml2Authentication(principal, token.getSaml2Response(),
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

}
