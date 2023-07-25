package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.AuthenticationRouteProperties;
import org.octri.authentication.config.ContentSecurityPolicyProperties;
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.config.SamlAuthenticationConfiguration;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.ApplicationAuthenticationFailureHandler;
import org.octri.authentication.server.security.ApplicationAuthenticationSuccessHandler;
import org.octri.authentication.server.security.AuthenticationUserDetailsService;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.octri.authentication.server.security.saml.SamlAuthenticationFailureHandler;
import org.octri.authentication.server.security.saml.SamlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.authentication.logout.Saml2LogoutRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity
@EnableAspectJAutoProxy
@EnableJpaAuditing
@PropertySource("classpath:authlib.properties")
@Configuration
public class DefaultSecurityConfigurer {

	private static final Log log = LogFactory.getLog(DefaultSecurityConfigurer.class);

	@Autowired
	private Boolean ldapEnabled;

	@Autowired
	private Boolean tableBasedEnabled;

	@Autowired
	private AuthenticationRouteProperties routes;

	@Autowired(required = false)
	private ContentSecurityPolicyProperties cspProperties;

	@Autowired
	private AuthenticationUserDetailsService userDetailsService;

	// Table-based authentication beans

	@Autowired
	private ApplicationAuthenticationSuccessHandler formAuthSuccessHandler;

	@Autowired
	private ApplicationAuthenticationFailureHandler formAuthFailureHandler;

	@Autowired(required = false)
	private TableBasedAuthenticationProvider tableBasedAuthenticationProvider;

	// LDAP authentication beans

	@Autowired(required = false)
	private LdapContextProperties ldapContextProperties;

	@Autowired(required = false)
	private LdapContextSource ldapContextSource;

	@Autowired(required = false)
	private UserDetailsContextMapper ldapUserDetailsContextMapper;

	@Autowired(required = false)
	private LdapAuthoritiesPopulator ldapAuthoritiesPopulator;

	// SAML authentication beans

	@Autowired(required = false)
	private SamlProperties samlProperties;

	@Autowired(required = false)
	private OpenSaml4AuthenticationProvider samlAuthenticationProvider;

	@Autowired(required = false)
	private SamlAuthenticationSuccessHandler samlAuthenticationSuccessHandler;

	@Autowired(required = false)
	private SamlAuthenticationFailureHandler samlAuthenticationFailureHandler;

	@Autowired(required = false)
	private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

	@Autowired(required = false)
	private Saml2LogoutRequestResolver saml2LogoutRequestResolver;

	@Autowired(required = false)
	private Saml2MetadataFilter saml2MetadataFilter;

	/**
	 * Provides a default security filter chain bean if the application does not provide a custom one. The behavior of
	 * the default filter chain is configured by other public methods in this class.
	 *
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
	@ConditionalOnMissingBean
	public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
		log.info("No security filter chain found. Providing default filter chain.");
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		configureAuthenticationManager(authBuilder);
		AuthenticationManager authManager = authBuilder.build();

		http.authenticationManager(authManager)
				.exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(routes.getLoginUrl()))
				.and()
				.csrf();

		configureContentSecurityPolicy(http);
		configureFormLoginWithDefaults(http);
		configureLogoutWithDefaults(http);
		configureRouteSecurityWithDefaults(http);

		configureSamlWithDefaults(http, authManager);

		return http.build();
	}

	/**
	 * Adds providers for all enabled authentication methods to the authentication manager builder.
	 *
	 * @param authBuilder
	 * @throws Exception
	 * @see configureSamlWithDefaults
	 */
	public void configureAuthenticationManager(AuthenticationManagerBuilder authBuilder)
			throws Exception {

		// Use SAML if enabled. Note that this is mostly configured using HTTP security methods.
		configureAuthenticationManagerForSaml(authBuilder);

		// If enabled, prefer table-based authentication for form authentication
		configureAuthenticationManagerForTableBased(authBuilder);

		// Form authentication falls through to LDAP if configured
		configureAuthenticationManagerForLdap(authBuilder);
	}

	/**
	 * Adds support for SAML authentication to the authentication manager.
	 *
	 * @param authBuilder
	 * @throws Exception
	 */
	public void configureAuthenticationManagerForSaml(AuthenticationManagerBuilder authBuilder) throws Exception {
		if (samlEnabled()) {
			log.info("Enabling SAML authentication.");
			authBuilder.authenticationProvider(samlAuthenticationProvider);
		} else {
			log.info("Not enabling SAML authentication: octri.authentication.saml.enabled was false.");
		}
	}

	/**
	 * Adds support for table-based authentication to the authentication manager.
	 *
	 * @param authBuilder
	 * @throws Exception
	 */
	public void configureAuthenticationManagerForTableBased(AuthenticationManagerBuilder authBuilder)
			throws Exception {
		if (tableBasedEnabled) {
			log.info("Enabling table-based authentication.");
			authBuilder.userDetailsService(userDetailsService).and()
					.authenticationProvider(tableBasedAuthenticationProvider);
		} else {
			log.info("Not enabling table-based authentication: octri.authentication.enable-table-based was false.");
		}
	}

	/**
	 * Adds support for LDAP authentication to the authentication manager.
	 *
	 * @param authBuilder
	 * @throws Exception
	 */
	public void configureAuthenticationManagerForLdap(AuthenticationManagerBuilder authBuilder) throws Exception {
		if (ldapEnabled) {
			log.info("Enabling LDAP authentication.");
			authBuilder.ldapAuthentication()
					.contextSource(ldapContextSource)
					.userSearchBase(ldapContextProperties.getSearchBase())
					.userSearchFilter(ldapContextProperties.getSearchFilter())
					.ldapAuthoritiesPopulator(ldapAuthoritiesPopulator)
					.userDetailsContextMapper(ldapUserDetailsContextMapper);
		} else {
			log.info("Not enabling LDAP authentication: octri.authentication.enable-ldap was false.");
		}
	}

	/**
	 * Configures default behavior for SAML authentication. By default, the SAML authentication response is validated to
	 * ensure that the user is a member of the configured group. Successful logins are logged to the `login_attempt`
	 * table, and the user is directed to the target URL or to the default success URL if no target is found in the
	 * session. Logins that fail because the user is not a member of the configured group are logged to the
	 * `login_attempt` table, and the user is redirected to the login failure URL.
	 * <br>
	 * Note that only logins that fail because of bad credentials are not logged, as those errors occur at the SAML IdP.
	 * <br>
	 * TODO: Use the database to validate users by default, rather than group membership.
	 *
	 * @param http
	 *            HttpSecurity builder
	 * @param authManager
	 *            the AuthenticationManager to user, e.g. from calling `AuthenticationManagerBuilder.build()`
	 * @throws Exception
	 * @see {@link SamlProperties}
	 * @see {@link SamlAuthenticationConfiguration}
	 */
	public void configureSamlWithDefaults(HttpSecurity http, AuthenticationManager authManager) throws Exception {
		if (!samlEnabled()) {
			return;
		}

		log.info("Configuring SAML authentication.");
		log.debug("SAML configuration: " + samlProperties);

		samlAuthenticationSuccessHandler.setDefaultTargetUrl(routes.getDefaultLoginSuccessUrl());
		samlAuthenticationFailureHandler.setDefaultFailureUrl(routes.getLoginFailureRedirectUrl());

		http
				.saml2Login(saml2 -> {
					saml2
							.authenticationManager(authManager)
							.relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
							.successHandler(samlAuthenticationSuccessHandler)
							.failureHandler(samlAuthenticationFailureHandler);
				})
				.saml2Logout(saml2 -> {
					saml2.logoutRequest(request -> request.logoutRequestResolver(saml2LogoutRequestResolver));
				})
				.addFilterBefore(saml2MetadataFilter, Saml2WebSsoAuthenticationFilter.class);
	}

	/**
	 * Configures default behavior for form authentication. Successful logins are logged to the `login_attempt` table,
	 * failed login metadata is reset, and the user is directed to the target URL or to the default success URL if no
	 * target is found in the session. Failed logins are logged to the `login_attempt` table, the user's failed login
	 * metadata is updated, and the user is redirected to the login failure URL.
	 *
	 * @param http
	 * @throws Exception
	 * @see {@link ApplicationRouteProperties}
	 * @see {@link ApplicationAuthenticationSuccessHandler}
	 * @see {@link ApplicationAuthenticationFailureHandler}
	 */
	public void configureFormLoginWithDefaults(HttpSecurity http) throws Exception {
		formAuthSuccessHandler.setDefaultTargetUrl(routes.getDefaultLoginSuccessUrl());
		formAuthFailureHandler.setDefaultFailureUrl(routes.getLoginFailureRedirectUrl());

		http.formLogin()
				.permitAll()
				.successHandler(formAuthSuccessHandler)
				.failureHandler(formAuthFailureHandler);
	}

	/**
	 * Configures default logout behavior. Requests to the logout URL trigger destroy the user's session and redirect to
	 * the logout success URL.
	 *
	 * @param http
	 * @throws Exception
	 * @see {@link ApplicationRouteProperties}
	 */
	public void configureLogoutWithDefaults(HttpSecurity http) throws Exception {
		http.logout()
				.permitAll()
				.logoutRequestMatcher(new AntPathRequestMatcher(routes.getLogoutUrl()))
				.logoutSuccessUrl(routes.getLogoutSuccessUrl());
	}

	/**
	 * Configures default route security. By default, public routes configured in {@link ApplicationRouteProperties}
	 * will allow anonymous access, and all other routes will require authentication. The HTTP PUT, POST, and PATCH
	 * methods will require authentication, and HTTP DELETE will be denied.
	 * <br>
	 * Most applications will need to restrict some routes using role-based access control, so using this method will
	 * not be appropriate if you are implementing a custom filter chain.
	 *
	 * @param http
	 * @throws Exception
	 * @see {@link ApplicationRouteProperties}
	 */
	public void configureRouteSecurityWithDefaults(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(routes.getPublicRoutesWithDefaults())
				.permitAll()
				.antMatchers(HttpMethod.POST).authenticated()
				.antMatchers(HttpMethod.PUT).authenticated()
				.antMatchers(HttpMethod.PATCH).authenticated()
				.antMatchers(HttpMethod.DELETE).denyAll()
				.anyRequest()
				.authenticated();
	}

	/**
	 * Configures the content security policy (CSP) header if CSP is enabled.
	 *
	 * @param http
	 * @throws Exception
	 * @see {@link ContentSecurityPolicyProperties}
	 */
	public void configureContentSecurityPolicy(HttpSecurity http) throws Exception {
		if (cspProperties == null || !cspProperties.isEnabled()) {
			log.info("Content security policy is disabled. Skipping.");
			return;
		}

		log.info("Configuring content security policy.");
		log.debug(cspProperties);
		http.headers((headers) -> headers.contentSecurityPolicy(csp -> {
			csp.policyDirectives(cspProperties.getPolicy());
			if (!cspProperties.isEnforced()) {
				csp.reportOnly();
			}
		}));
	}

	private boolean samlEnabled() {
		return samlProperties != null && Boolean.TRUE.equals(samlProperties.getEnabled());
	}

}
