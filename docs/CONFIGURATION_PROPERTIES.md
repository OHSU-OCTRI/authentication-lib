# Configuration Properties

This file documents configuration properties that control AuthLib behavior, including both properties implemented in AuthLib itself and relevant [Spring Security](https://docs.spring.io/spring-security/reference/6.2/index.html) properties.

## Basic Properties

The properties below are used to configure basic functionality of the library, including the authentication methods used.

At least one of the authentication methods must be enabled (see `octri.authentication.enable-ldap`, `octri.authentication.enable-table-based`, and `octri.authentication.saml.enabled`).

| Property | Environment Variable | Type | Default value | Description |
| - | - | - | - | - |
| octri.authentication.base-url | OCTRI_AUTHENTICATION_BASEURL | string | http://localhost:8080 | Base URL of the application, without the context path. Used to construct URLs, particularly in email messages. An error is logged if this is set to the default value to encourage proper configuration. |
| octri.authentication.credentials-expiration-period | OCTRI_AUTHENTICATION_CREDENTIALSEXPIRATIONPERIOD | integer | 180 | Length of time (in days) that table-based credentials are valid. After this period has elapsed, users will be required to change their password. |
| octri.authentication.custom-role-script | OCTRI_AUTHENTICATION_CUSTOM_ROLE_SCRIPT | string | None | Path to custom JavaScript to use when validating user roles. Path should be relative to the application context path. Only relevant when `octri.authentication.role-style=custom`. |
| octri.authentication.email-dry-run | OCTRI_AUTHENTICATION_EMAILDRYRUN | boolean | FALSE | Whether user account emails should be logged to the console instead of being sent. |
| octri.authentication.email-required | OCTRI_AUTHENTICATION_EMAILREQUIRED | boolean | TRUE | Whether the email field on the user form should be treated as required. Applications wishing to make email optional should run the additional migration scripts in `setup/optional_migrations/noemail/`. |
| octri.authentication.enable-ldap | OCTRI_AUTHENTICATION_ENABLELDAP | boolean | None | Whether LDAP authentication is enabled. See LDAP Authentication below for more properties. |
| octri.authentication.enable-table-based | OCTRI_AUTHENTICATION_ENABLETABLEBASED | boolean | None | Whether table-based authentication is enabled. |
| octri.authentication.max-login-attempts | OCTRI_AUTHENTICATION_MAXLOGINATTEMPTS | integer | 7 | Number of failed login attempts allowed before an account is locked. |
| octri.authentication.password-token-valid-for | OCTRI_AUTHENTICATION_PASSWORD_TOKEN_VALID_FOR | duration | 30m | Length of time that password reset tokens will be valid. See the [Spring Boot documentation](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.conversion.durations) for valid formats. |
| octri.authentication.role-style | OCTRI_AUTHENTICATION_ROLE_STYLE | string | multiple | Role style. Determines how the role selector on the user form is rendered. Valid options are `single`, `multiple`, or `custom`. |
| octri.authentication.username-style | OCTRI_AUTHENTICATION_USERNAMESTYLE | string | plain | Username style. Valid options are `plain`, `email`, or `mixed`. |

## Route Configuration

The properties below are used to configure default routing behavior. If you need to customize your application's routing beyond what can be accomplished just using these properties, your application should provide its own [`SecurityFilterChain`](https://docs.spring.io/spring-security/reference/6.2/index.html) bean. See [DefaultSecurityConfigurer.java](../authentication_lib/src/main/java/org/octri/authentication/DefaultSecurityConfigurer.java) for the default behavior and reusable configuration methods.

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| octri.authentication.routes.login-url | OCTRI_AUTHENTICATION_ROUTES_LOGINURL | string | `/login` | Path of the login page |
| octri.authentication.routes.default-login-success-url | OCTRI_AUTHENTICATION_ROUTES_DEFAULTLOGINSUCCESSURL | string | `/admin/user/list` | Path that the application redirects to after login |
| octri.authentication.routes.login-failure-redirect-url | OCTRI_AUTHENTICATION_ROUTES_LOGINFAILUREREDIRECTURL | string | `/login?error` | Path that the application redirects to after login failure |
| octri.authentication.routes.logout-url | OCTRI_AUTHENTICATION_ROUTES_LOGOUTURL | string | `/logout` | Path of the logout page |
| octri.authentication.routes.logout-success-url | OCTRI_AUTHENTICATION_ROUTES_LOGOUTSUCCESSURL | string | `/login` | Path that the application redirects to after logout |
| octri.authentication.routes.custom-public-routes | OCTRI_AUTHENTICATION_ROUTES_CUSTOMPUBLICROUTES | list of string | `[]` | List of custom routes that do not require authentication. See also [`AuthenticationRouteProperties.DEFAULT_PUBLIC_ROUTES`](../authentication_lib/src/main/java/org/octri/authentication/config/AuthenticationRouteProperties.java). |

## Content Security Policy

_Experimental_: The properties below are used to configure the `Content-Security-Policy` header.

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| octri.authentication.csp.enabled | OCTRI_AUTHENTICATION_CSP_ENABLED | boolean | `false` | Whether to enable the `Content-Security-Policy` header |
| octri.authentication.csp.enforced | OCTRI_AUTHENTICATION_CSP_ENFORCED | boolean | `false` | Whether to enforce the configured policy. When true, resources that violate the policy will not be loaded. When false, policy violations will only be logged to the browser console. |
| octri.authentication.csp.policy | OCTRI_AUTHENTICATION_CSP_POLICY | string | `default-src 'self'; img-src 'self' data:` | Allows resources from the current page origin, and images from the current origin or `data:` URLs. |

## Email Configuration

The properties below are used to configure how the library sends account setup and password reset emails.

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| spring.mail.enabled | SPRING_MAIL_ENABLED | boolean | None | Whether email delivery is enabled |
| spring.mail.from | SPRING_MAIL_FROM | string | None | Email address to use in the From: line |
| spring.mail.defaultEncoding | SPRING_MAIL_DEFAULT_ENCODING | string | None | Default character encoding to use for messages |
| spring.mail.host | SPRING_MAIL_HOST | string | None | Email server host |
| spring.mail.port | SPRING_MAIL_PORT | integer | None | Email server port |
| spring.mail.protocol | SPRING_MAIL_PROTOCOL | string | None | Email server protocol |
| spring.mail.testConnection | SPRING_MAIL_TEST_CONNECTION | boolean | None | Whether to periodically test the connection to the email server |
| spring.mail.username | SPRING_MAIL_USERNAME | string | None | Email server username |
| spring.mail.password | SPRING_MAIL_PASSWORD | string | None | Email server password |
| spring.mail.properties.mail.smtp.auth | SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH | boolean | None | Whether the email server requires authentication |
| spring.mail.properties.mail.smtp.starttls.enable | SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE | boolean | None | Whether to use STARTTLS when connecting to the email server |
| spring.mail.properties.mail.smtp.starttls.required | SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_REQUIRED | boolean | None | Whether to require STARTTLS when connecting to the email server |

## LDAP Authentication

The properties below are used to configure how the library binds to the LDAP directory and searches for user accounts. These properties have no effect if `octri.authentication.enable-ldap` is false.

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| ldap.context-source.url | LDAP_CONTEXTSOURCE_URL | string | None | `ldap://...` or `ldaps://...` URL of the LDAP server |
| ldap.context-source.userDn | LDAP_CONTEXTSOURCE_USERDN | string | None | LDAP distinguished name (DN) of the user to use to connect to the LDAP server |
| ldap.context-source.password | LDAP_CONTEXTSOURCE_PASSWORD | string | None | LDAP password of the user to use to connect to the LDAP server |
| ldap.context-source.searchBase | LDAP_CONTEXTSOURCE_SEARCHBASE | string | None | Directory subtree path to search for user accounts |
| ldap.context-source.searchFilter | LDAP_CONTEXTSOURCE_SEARCHFILTER | string | None | Optional filter expression to use when searching for user accounts |
| ldap.context-source.organization | LDAP_CONTEXTSOURCE_ORGANIZATION | string | None | Arbitrary string; used to populate the `organization` field of LDAP user accounts |
| ldap.context-source.emailDomain | LDAP_CONTEXTSOURCE_EMAILDOMAIN | string | None | Email domain name that LDAP accounts belong to |

## SAML Authentication

The properties below are used to configure [SAML authentication](https://docs.spring.io/spring-security/reference/6.2/servlet/saml2/index.html).

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| octri.authentication.saml.enabled | OCTRI_AUTHENTICATION_SAML_ENABLED | boolean | `false` | Whether SAML authentication is enabled. |
| octri.authentication.saml.registrationId | OCTRI_AUTHENTICATION_SAML_REGISTRATION_ID | string | `default` | ID of the relying party registration. Defaults to "default". The relying party ID is included in the login and metadata URLs and cannot be changed once registered with the IdP. |
| octri.authentication.saml.signingKeyLocation | OCTRI_AUTHENTICATION_SAML_SIGNING_KEY_LOCATION | string | None | Location of the private key for the X509 certificate to use to sign requests. Most likely a file URI or classpath location. Key must be in PEM-encoded PKCS#8 format, beginning with "-----BEGIN PRIVATE KEY-----". |
| octri.authentication.saml.signingCertLocation | OCTRI_AUTHENTICATION_SAML_SIGNING_CERT_LOCATION | string | None | Location of the X509 certificate to use to sign requests. Most likely a file URI or classpath location. Must be PEM-encoded X509 format, beginning with "-----BEGIN CERTIFICATE-----". |
| octri.authentication.saml.decryptionKeyLocation | OCTRI_AUTHENTICATION_SAML_DECRYPTION_KEY_LOCATION | string | None | Location of the private key for the X509 certificate to use to decrypt requests. Most likely a file URI or classpath location. Key must be in PEM-encoded PKCS#8 format, beginning with "-----BEGIN PRIVATE KEY-----". |
| octri.authentication.saml.decryptionCertLocation | OCTRI_AUTHENTICATION_SAML_DECRYPTION_CERT_LOCATION | string | None | Location of the X509 certificate to use to decrypt requests. Most likely a file URI or classpath location. Must be PEM-encoded X509 format, beginning with "-----BEGIN CERTIFICATE-----". |
| octri.authentication.saml.idpMetadataUri | OCTRI_AUTHENTICATION_SAML_IDP_METADATA_URI | string | None | URI of the IdP's metadata XML. |
| octri.authentication.saml.requiredGroup | OCTRI_AUTHENTICATION_SAML_REQUIRED_GROUP | string | None | Users must be a member of this group to access the application. |
| octri.authentication.saml.useridAttribute | OCTRI_AUTHENTICATION_SAML_USERID_ATTRIBUTE | string | `urn:oid:0.9.2342.19200300.100.1.1` | ID of the SAML assertion attribute that stores the principal's userid / username. |
| octri.authentication.saml.emailAttribute | OCTRI_AUTHENTICATION_SAML_EMAIL_ATTRIBUTE | string | `urn:oid:0.9.2342.19200300.100.1.3` | ID of the SAML assertion attribute that stores the principal's email address. |
| octri.authentication.saml.firstNameAttribute | OCTRI_AUTHENTICATION_SAML_FIRST_NAME_ATTRIBUTE | string | `urn:oid:2.5.4.42` | ID of the SAML assertion attribute that stores the principal's first name. |
| octri.authentication.saml.lastNameAttribute | OCTRI_AUTHENTICATION_SAML_LAST_NAME_ATTRIBUTE | string | `urn:oid:2.5.4.4` | ID of the SAML assertion attribute that stores the principal's last name. |
| octri.authentication.saml.groupAttribute | OCTRI_AUTHENTICATION_SAML_GROUP_ATTRIBUTE | string | `role` | ID of the SAML assertion attribute that stores the principal's group membership information. |
| octri.authentication.saml.logoutPath | OCTRI_AUTHENTICATION_SAML_LOGOUT_PATH | string | `{baseUrl}/logout/saml2/slo` | Path of the SAML single log out (SLO) endpoint. |

## Template Configuration

The Mustache templates used to render the library's user interface are provided by a separate package (`authentication_ui_bootstrap5`). To customize the appearance of pages rendered by the library, the templates can be overridden in part or in full. The property below is provided to facilitate this process.

See [`TemplateConfiguration.TEMPLATE_PATHS`](../authentication_lib/src/main/java/org/octri/authentication/config/TemplateConfiguration.java) for the full list of paths, and see the [`mustache-templates` directory of `authentication_ui_bootstrap5`](../authentication_ui_bootstrap5/src/main/resources/mustache-templates/) for example templates.

| Property | Environment variable | Type | Default value | Description |
| - | - | - | - | - |
| octri.authentication.ui.check-templates | OCTRI_AUTHENTICATION_UI_CHECKTEMPLATES | boolean | `true` | Whether to warn if any Mustache templates are missing from the classpath. |
