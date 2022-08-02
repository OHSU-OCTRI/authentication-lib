package org.octri.authentication.server.security.saml;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.SecurityHelper.Role;
import org.octri.authentication.server.security.entity.User;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
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

    private static final long SAML_USER_SENTINEL = -999L;

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

        User user = new User();
        user.setId(SAML_USER_SENTINEL);
        user.setUsername(AssertionUtils.getAttributeValue(attributes, samlProperties.getUseridAttribute()));
        user.setFirstName(AssertionUtils.getAttributeValue(attributes, samlProperties.getFirstNameAttribute()));
        user.setLastName(AssertionUtils.getAttributeValue(attributes, samlProperties.getLastNameAttribute()));
        user.setEmail(AssertionUtils.getAttributeValue(attributes, samlProperties.getEmailAttribute()));
        user.setInstitution(token.getRelyingPartyRegistration().getRegistrationId());

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(Role.ROLE_USER.name());

        ApplicationSaml2AuthenticatedPrincipal principal = new ApplicationSaml2AuthenticatedPrincipal(user, authorities,
                nameId, attributes);

        principal.setRelyingPartyRegistrationId(token.getRelyingPartyRegistration().getRegistrationId());

        log.debug("Logging in SAML2 principal: " + principal);
        return new Saml2Authentication(principal, token.getSaml2Response(), authorities);
    }

}
