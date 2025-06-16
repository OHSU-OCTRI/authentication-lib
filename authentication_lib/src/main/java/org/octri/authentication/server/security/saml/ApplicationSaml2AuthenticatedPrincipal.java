package org.octri.authentication.server.security.saml;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.octri.authentication.server.security.AuthenticationUserDetails;
import org.octri.authentication.server.security.entity.User;
import org.opensaml.saml.saml2.core.NameID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

/**
 * Custom implementation of {@link Saml2AuthenticatedPrincipal} with additional user details. Captures NameID values
 * needed for SAML logout and user attributes.
 */
public class ApplicationSaml2AuthenticatedPrincipal extends AuthenticationUserDetails
		implements Saml2AuthenticatedPrincipal {

	private static final long serialVersionUID = -7394856325865885172L;

	/**
	 * The NameID the SAML IdP used to identify the user.
	 */
	private final NameID nameId;

	/**
	 * Attributes included in the SAML assertion.
	 */
	private final Map<String, List<Object>> attributes;

	/**
	 * ID of the relying party registration that authenticated the user.
	 */
	private String relyingPartyRegistrationId;

	/**
	 * The user's first name (given name).
	 */
	private String firstName;

	/**
	 * The user's last name (family name, surname).
	 */
	private String lastName;

	/**
	 * The user's email address.
	 */
	private String email;

	/**
	 * Constructor
	 *
	 * @param user
	 *            user entity
	 * @param authorities
	 *            the user's authorities (e.g. roles)
	 * @param nameId
	 *            the SAML NameID provided by the IdP
	 * @param attributes
	 *            attributes included in the SAML assertion
	 */
	public ApplicationSaml2AuthenticatedPrincipal(User user, Collection<? extends GrantedAuthority> authorities,
			NameID nameId, Map<String, List<Object>> attributes) {
		super(user, authorities);
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.nameId = nameId;
		this.attributes = attributes;
		this.relyingPartyRegistrationId = null;
	}

	/**
	 * Returns the authenticated principal's username.
	 */
	@Override
	public String getName() {
		return this.getUsername();
	}

	/**
	 * Returns attributes included in the SAML assertion.
	 */
	@Override
	public Map<String, List<Object>> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns the ID of the {@link RelyingPartyRegistration} associated with the login request.
	 */
	@Override
	public String getRelyingPartyRegistrationId() {
		return this.relyingPartyRegistrationId;
	}

	/**
	 * Sets the ID of the {@link RelyingPartyRegistration} associated with the login request.
	 *
	 * @param relyingPartyRegistrationId
	 *            string identifying the {@link RelyingPartyRegistration}
	 */
	public void setRelyingPartyRegistrationId(String relyingPartyRegistrationId) {
		this.relyingPartyRegistrationId = relyingPartyRegistrationId;
	}

	/**
	 * @return the NameID the SAML IdP used to identify the user
	 */
	public NameID getNameId() {
		return this.nameId;
	}

	/**
	 * @return the user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the user's first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the user's last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the user's email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the user's email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "ApplicationSaml2AuthenticatedPrincipal [nameId=" + nameId + ", attributes=" + attributes
				+ ", relyingPartyRegistrationId=" + relyingPartyRegistrationId + ", username=" + getUsername()
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
	}

}
