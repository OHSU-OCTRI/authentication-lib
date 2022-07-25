package org.octri.authentication.server.security.saml;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.opensaml.saml.saml2.core.NameID;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

/**
 * Custom implementation of {@link Saml2AuthenticatedPrincipal} with additional user details. Captures NameID values
 * needed for SAML logout and user attributes.
 */
public class ApplicationSaml2AuthenticatedPrincipal implements Saml2AuthenticatedPrincipal, Serializable {

	private static final long serialVersionUID = -7394856325865885172L;

	private final NameID nameId;

	private final Map<String, List<Object>> attributes;

	private String relyingPartyRegistrationId;

	private String username;

	private String firstName;

	private String lastName;

	private String email;

	public ApplicationSaml2AuthenticatedPrincipal(NameID nameId, Map<String, List<Object>> attributes) {
		this.nameId = nameId;
		this.attributes = attributes;
		this.relyingPartyRegistrationId = null;
	}

	@Override
	public String getName() {
		return this.username;
	}

	@Override
	public Map<String, List<Object>> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getRelyingPartyRegistrationId() {
		return this.relyingPartyRegistrationId;
	}

	public void setRelyingPartyRegistrationId(String relyingPartyRegistrationId) {
		this.relyingPartyRegistrationId = relyingPartyRegistrationId;
	}

	public NameID getNameId() {
		return this.nameId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ApplicationSaml2AuthenticatedPrincipal [nameId=" + nameId + ", attributes=" + attributes
				+ ", relyingPartyRegistrationId=" + relyingPartyRegistrationId + ", username=" + username
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
	}

}
