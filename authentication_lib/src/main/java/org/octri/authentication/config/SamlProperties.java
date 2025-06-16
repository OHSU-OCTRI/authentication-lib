package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for SAML authentication.
 */
@ConfigurationProperties(prefix = "octri.authentication.saml")
public class SamlProperties {

	/**
	 * Whether to enable SAML authentication. Defaults to false.
	 */
	private Boolean enabled = false;

	/**
	 * ID of the relying party registration. Defaults to "default".
	 * <br>
	 * The relying party ID is included in the login and metadata URLs and cannot be changed once registered with the
	 * IdP. Example URLs:
	 * <br>
	 * - Metadata: <code>{{contextPath}}/saml2/service-provider-metadata/{{registrationId}}</code><br>
	 * - Login initiation: <code>{{contextPath}}/saml2/authenticate/{{registrationId}}</code><br>
	 * - Login completion: <code>{{contextPath}}/login/saml2/sso/{{registrationId}}</code>
	 */
	private String registrationId = "default";

	/**
	 * Location of the private key for the X509 certificate to use to sign requests. Most likely a file URI or
	 * classpath location. Key must be in PEM-encoded PKCS#8 format, beginning with "-----BEGIN PRIVATE KEY-----".
	 */
	private Resource signingKeyLocation;

	/**
	 * Location of the X509 certificate to use to sign requests. Most likely a file URI or classpath location. Must be
	 * PEM-encoded X509 format, beginning with "-----BEGIN CERTIFICATE-----".
	 */
	private Resource signingCertLocation;

	/**
	 * Location of the private key for the X509 certificate to use to decrypt requests. Most likely a file URI or
	 * classpath location. Key must be in PEM-encoded PKCS#8 format, beginning with "-----BEGIN PRIVATE KEY-----".
	 */
	private Resource decryptionKeyLocation;

	/**
	 * Location of the X509 certificate to use to decrypt requests. Most likely a file URI or classpath location. Must
	 * be
	 * PEM-encoded X509 format, beginning with "-----BEGIN CERTIFICATE-----".
	 */
	private Resource decryptionCertLocation;

	/**
	 * URI of the IdP's metadata XML.
	 */
	private String idpMetadataUri;

	/**
	 * Users must be a member of this group to access the application.
	 */
	private String requiredGroup;

	/**
	 * ID of the SAML assertion attribute that stores the principal's userid / username. Defaults to
	 * "urn:oid:0.9.2342.19200300.100.1.1".
	 */
	private String useridAttribute = "urn:oid:0.9.2342.19200300.100.1.1";

	/**
	 * ID of the SAML assertion attribute that stores the principal's email address. Defaults to
	 * "urn:oid:0.9.2342.19200300.100.1.3".
	 */
	private String emailAttribute = "urn:oid:0.9.2342.19200300.100.1.3";

	/**
	 * ID of the SAML assertion attribute that stores the principal's first name. Defaults to "urn:oid:2.5.4.42".
	 */
	private String firstNameAttribute = "urn:oid:2.5.4.42";

	/**
	 * ID of the SAML assertion attribute that stores the principal's last name. Defaults to "urn:oid:2.5.4.4";
	 */
	private String lastNameAttribute = "urn:oid:2.5.4.4";

	/**
	 * ID of the SAML assertion attribute that stores the principal's group membership information. Defaults to "role".
	 */
	private String groupAttribute = "role";

	/**
	 * Path of the SAML single log out (SLO) endpoint. Defaults to "{baseUrl}/logout/saml2/slo".
	 */
	private String logoutPath = "{baseUrl}/logout/saml2/slo";

	/**
	 * Gets whether SAML authentication is enabled.
	 * 
	 * @return true if SAML auth is enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets whether SAML authentication is enabled.
	 * 
	 * @param enabled
	 *            true if enabled, else false
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the ID of the relying party registration.
	 * 
	 * @return ID of the relying party registration
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Sets the ID of the relying party registration.
	 * 
	 * @param registrationId
	 *            the ID of the relying party registration
	 */
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	/**
	 * Gets the location of the RSA signing key file.
	 *
	 * @return the location of the RSA signing key file
	 */
	public Resource getSigningKeyLocation() {
		return signingKeyLocation;
	}

	/**
	 * Sets the location of the RSA signing key file.
	 * 
	 * @param signingKeyLocation
	 *            the location of the RSA signing key file
	 */
	public void setSigningKeyLocation(Resource signingKeyLocation) {
		this.signingKeyLocation = signingKeyLocation;
	}

	/**
	 * Gets the location of the signing X509 certificate file.
	 *
	 * @return the location of the signing X509 certificate file
	 */
	public Resource getSigningCertLocation() {
		return signingCertLocation;
	}

	/**
	 * Sets the location of the signing X509 certificate file.
	 * 
	 * @param signingCertLocation
	 *            the location of the signing X509 certificate file
	 */
	public void setSigningCertLocation(Resource signingCertLocation) {
		this.signingCertLocation = signingCertLocation;
	}

	/**
	 * Gets the location of the RSA decryption key file.
	 * 
	 * @return the location of the RSA decryption key file
	 */
	public Resource getDecryptionKeyLocation() {
		return decryptionKeyLocation;
	}

	/**
	 * Sets the location of the RSA decryption key file.
	 * 
	 * @param decryptionKeyLocation
	 *            the location of the RSA decryption key file
	 */
	public void setDecryptionKeyLocation(Resource decryptionKeyLocation) {
		this.decryptionKeyLocation = decryptionKeyLocation;
	}

	/**
	 * Gets the location of the decryption X509 certificate file.
	 * 
	 * @return the location of the decryption X509 certificate file
	 */
	public Resource getDecryptionCertLocation() {
		return decryptionCertLocation;
	}

	/**
	 * Sets the location of the decryption X509 certificate file.
	 * 
	 * @param decryptionCertLocation
	 *            the location of the decryption X509 certificate file
	 */
	public void setDecryptionCertLocation(Resource decryptionCertLocation) {
		this.decryptionCertLocation = decryptionCertLocation;
	}

	/**
	 * Gets the IdP metadata URI.
	 * 
	 * @return the IdP metadata URI
	 */
	public String getIdpMetadataUri() {
		return idpMetadataUri;
	}

	/**
	 * Sets the URI of the IdP metadata.
	 * 
	 * @param idpMetadataUri
	 *            the URI of the IdP metadata
	 */
	public void setIdpMetadataUri(String idpMetadataUri) {
		this.idpMetadataUri = idpMetadataUri;
	}

	/**
	 * Gets the name of the group required for login.
	 * 
	 * @return the name of the group required for login
	 */
	public String getRequiredGroup() {
		return requiredGroup;
	}

	/**
	 * Sets the name of the group required for login.
	 * 
	 * @param requiredGroup
	 *            the name of the group required for login
	 */
	public void setRequiredGroup(String requiredGroup) {
		this.requiredGroup = requiredGroup;
	}

	/**
	 * Gets the ID of the SAML attribute used to provide the user ID.
	 * 
	 * @return the ID of the SAML attribute used to provide the user ID
	 */
	public String getUseridAttribute() {
		return useridAttribute;
	}

	/**
	 * Sets the ID of the SAML attribute used to provide the user ID.
	 * 
	 * @param useridAttribute
	 *            the ID of the SAML attribute used to provide the user ID
	 */
	public void setUseridAttribute(String useridAttribute) {
		this.useridAttribute = useridAttribute;
	}

	/**
	 * Gets the ID of the SAML attribute used to provide the user's email.
	 * 
	 * @return the ID of the SAML attribute used to provide the user's email
	 */
	public String getEmailAttribute() {
		return emailAttribute;
	}

	/**
	 * Sets the ID of the SAML attribute used to provide the user's email.
	 * 
	 * @param emailAttribute
	 *            the ID of the SAML attribute used to provide the user's email
	 */
	public void setEmailAttribute(String emailAttribute) {
		this.emailAttribute = emailAttribute;
	}

	/**
	 * Gets the ID of the SAML attribute used to provide the user's first name.
	 * 
	 * @return the ID of the SAML attribute used to provide the user's first name
	 */
	public String getFirstNameAttribute() {
		return firstNameAttribute;
	}

	/**
	 * Sets the ID of the SAML attribute used to provide the user's first name.
	 * 
	 * @param firstNameAttribute
	 *            the ID of the SAML attribute used to provide the user's first name
	 */
	public void setFirstNameAttribute(String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}

	/**
	 * Gets the ID of the SAML attribute used to provide the user's last name.
	 * 
	 * @return the ID of the SAML attribute used to provide the user's last name
	 */
	public String getLastNameAttribute() {
		return lastNameAttribute;
	}

	/**
	 * Sets the ID of the SAML attribute used to provide the user's last name.
	 * 
	 * @param lastNameAttribute
	 *            the ID of the SAML attribute used to provide the user's last name
	 */
	public void setLastNameAttribute(String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}

	/**
	 * Gets the ID of the SAML attribute used to provide the user's group assignments.
	 * 
	 * @return the ID of the SAML attribute used to provide the user's group assignments
	 */
	public String getGroupAttribute() {
		return groupAttribute;
	}

	/**
	 * Sets the ID of the SAML attribute used to provide the user's group assignments.
	 * 
	 * @param groupAttribute
	 *            the ID of the SAML attribute used to provide the user's group assignments
	 */
	public void setGroupAttribute(String groupAttribute) {
		this.groupAttribute = groupAttribute;
	}

	/**
	 * Gets the configured SAML logout path.
	 * 
	 * @return the configured SAML logout path
	 */
	public String getLogoutPath() {
		return logoutPath;
	}

	/**
	 * Sets the SAML logout path.
	 * 
	 * @param logoutPath
	 *            the SAML logout path
	 */
	public void setLogoutPath(String logoutPath) {
		this.logoutPath = logoutPath;
	}

	@Override
	public String toString() {
		return "SamlProperties [decryptionCertLocation=" + decryptionCertLocation + ", decryptionKeyLocation="
				+ decryptionKeyLocation + ", emailAttribute=" + emailAttribute + ", enabled=" + enabled
				+ ", firstNameAttribute=" + firstNameAttribute + ", groupAttribute=" + groupAttribute
				+ ", idpMetadataUri=" + idpMetadataUri + ", lastNameAttribute=" + lastNameAttribute + ", logoutPath="
				+ logoutPath + ", registrationId=" + registrationId + ", requiredGroup=" + requiredGroup
				+ ", signingCertLocation=" + signingCertLocation + ", signingKeyLocation=" + signingKeyLocation
				+ ", useridAttribute=" + useridAttribute + "]";
	}

}
