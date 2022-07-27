package org.octri.authentication.config;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.saml.ApplicationSaml2AuthenticatedPrincipal;
import org.octri.authentication.server.security.saml.GroupMembershipSamlResponseValidator;
import org.octri.authentication.server.security.saml.SamlResponseUserDetailsAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.core.Saml2X509Credential.Saml2X509CredentialType;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.logout.OpenSaml4LogoutRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestResolver;
import org.springframework.util.Assert;

/**
 * Configuration for SAML authentication.
 */
@Configuration
@EnableConfigurationProperties(SamlProperties.class)
@ConditionalOnProperty(value = "octri.authentication.saml.enabled", havingValue = "true", matchIfMissing = false)
public class SamlAuthenticationConfiguration {

	private static final Log log = LogFactory.getLog(SamlAuthenticationConfiguration.class);

	@Autowired
	private SamlProperties samlProperties;

	/**
	 * Creates a SAML 2 authentication provider with customized assertion validation and authentication conversion.
	 *
	 * TODO: Document the behavior of the default configuration and how to override it here
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public OpenSaml4AuthenticationProvider defaultSamlAuthenticationProvider() {
		log.debug("Creating default SAML authentication provider");
		var authenticationConverter = new SamlResponseUserDetailsAuthenticationConverter(samlProperties);
		var responseValidator = new GroupMembershipSamlResponseValidator(samlProperties);
		var authenticationProvider = new OpenSaml4AuthenticationProvider();
		authenticationProvider.setResponseAuthenticationConverter(authenticationConverter);
		authenticationProvider.setAssertionValidator(responseValidator);
		return authenticationProvider;
	}

	/**
	 * Creates a default registration repository configured with credentials for the SP and IdP.
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RelyingPartyRegistrationRepository defaultRelyingPartyRegistrationRepository() {
		log.debug("Creating default RelyingPartyRegistrationRepository");
		var registration = RelyingPartyRegistrations
				.fromMetadataLocation(samlProperties.getIdpMetadataUri())
				.registrationId(samlProperties.getRegistrationId())
				.decryptionX509Credentials(c -> c.add(loadDecryptionCredential()))
				.signingX509Credentials(c -> c.add(loadSigningCredential()))
				.build();
		return new InMemoryRelyingPartyRegistrationRepository(registration);
	}

	/**
	 * Creates a registration resolver from the given registration repository.
	 *
	 * @param repository
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RelyingPartyRegistrationResolver defaultRelyingPartyRegistrationResolver(
			RelyingPartyRegistrationRepository repository) {
		log.debug("Creating default RelyingPartyRegistrationResolver");
		return new DefaultRelyingPartyRegistrationResolver(repository);
	}

	/**
	 * Creates a custom logout request resolver that populates the logout request's required NameID format.
	 *
	 * @param resolver
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public Saml2LogoutRequestResolver defaultLogoutRequestResolver(RelyingPartyRegistrationResolver resolver) {
		var logoutRequestResolver = new OpenSaml4LogoutRequestResolver(resolver);

		logoutRequestResolver.setParametersConsumer((parameters) -> {
			var principal = ((ApplicationSaml2AuthenticatedPrincipal) parameters
					.getAuthentication().getPrincipal());
			log.debug("Logging out principal: " + principal);
			var logoutRequest = parameters.getLogoutRequest();
			var originalNameId = principal.getNameId();
			var logoutNameId = logoutRequest.getNameID();

			// the original NameID cannot be reused, so copy its values to the one in the request
			logoutNameId.setValue(originalNameId.getValue());
			logoutNameId.setFormat(originalNameId.getFormat());
			logoutNameId.setNameQualifier(originalNameId.getNameQualifier());
			logoutNameId.setSPNameQualifier(originalNameId.getSPNameQualifier());
		});

		return logoutRequestResolver;
	}

	/**
	 * Creates a request filter that generates SAML Service Provider (SP) metadata XML.
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public Saml2MetadataFilter saml2MetadataFilter(RelyingPartyRegistrationResolver resolver) {
		return new Saml2MetadataFilter(resolver, new OpenSamlMetadataResolver());
	}

	/**
	 * Loads the application's SAML decryption credential from the configured private key and certificate locations.
	 *
	 * @return
	 */
	private Saml2X509Credential loadDecryptionCredential() {
		var key = readPrivateKey(samlProperties.getDecryptionKeyLocation());
		var cert = readCertificate(samlProperties.getDecryptionCertLocation());
		return new Saml2X509Credential(key, cert, Saml2X509CredentialType.DECRYPTION);
	}

	/**
	 * Loads the application's SAML signing credential from the configured private key and certificate locations.
	 *
	 * @return
	 */
	private Saml2X509Credential loadSigningCredential() {
		var key = readPrivateKey(samlProperties.getSigningKeyLocation());
		var cert = readCertificate(samlProperties.getSigningCertLocation());
		return new Saml2X509Credential(key, cert, Saml2X509CredentialType.SIGNING);
	}

	/**
	 * Attempts to read a private key from the given resource. The location referenced should contain PEM-encoded RSA
	 * private key data in PKCS #8 format.
	 *
	 * @param location
	 * @return
	 */
	private RSAPrivateKey readPrivateKey(Resource location) {
		Assert.notNull(location, "Key location cannot be null");
		try {
			var inputStream = location.getInputStream();
			return RsaKeyConverters.pkcs8().convert(inputStream);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Attempts to read an X509 certificate from the given resource. The location referenced should contain PEM-encoded
	 * X509 certificate data.
	 *
	 * @param location
	 * @return
	 */
	private X509Certificate readCertificate(Resource location) {
		Assert.notNull(location, "Certificate location cannot be null");
		try {
			var inputStream = location.getInputStream();
			return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(inputStream);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
