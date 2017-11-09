# OCTRI Authentication Library

This library allows you to use the entities and services provided to bootstrap authentication into a new Spring Boot application. Domain classes for users and roles are provided, along with Flyway migrations to initiate the database. Once configured, your application will have endpoints for login and user management. In addition, failure and success handlers are provided that will persist all login attempts with IP addresses for auditing purposes.

## Getting started

The repo 'example_auth_project' in this project shows the minimum configuration needed to use the library. First, add this dependency to your pom:

```
		<dependency>
			<groupId>org.octri.authentication</groupId>
			<artifactId>authentication_lib</artifactId>
			<version>${authentication_lib.version}</version>
		</dependency>
``` 

The library requires one property to be set that configures the number of login attempts allowed before a user is locked out. (TODO: This should be optional.) In application.properties set:

```
octri.authentication.max-login-attempts=3
```

The Spring Boot Runner needs to set some additional parameters to ensure that domain, repositories, and autowired components for the Authentication Library are picked up:

```
@SpringBootApplication
@ComponentScan({"org.octri.test", "org.octri.authentication"})
@EntityScan( basePackages = {"org.octri.test", "org.octri.authentication.server.security.entity"} )
@EnableJpaRepositories("org.octri.authentication.server.security.repository")
public class TestProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestProjectApplication.class, args);
	}
}
```

Now your application can define its Security Configuration using the autowired components available in the library. Here's an example of LDAP Only

```
@Configuration
@EnableConfigurationProperties
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final Log log = LogFactory.getLog(SecurityConfiguration.class);

	@Value("${server.context-path}")
	private String contextPath;

	@Value("${ldap.contextSource.searchBase}")
	private String ldapSearchBase;

	@Value("${ldap.contextSource.searchFilter}")
	private String ldapSearchFilter;

	@Autowired
	AuthenticationUserDetailsService userDetailsService;

	@Autowired
	private StatusOnlyAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JsonResponseAuthenticationSuccessHandler authSuccessHandler;

	@Autowired
	private JsonResponseAuthenticationFailureHandler authFailureHandler;

	@Autowired
	private StatusOnlyLogoutSuccessHandler logoutSuccessHandler;

	@Bean
	@ConfigurationProperties(prefix = "ldap.contextSource")
	public BaseLdapPathContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		return contextSource;
	}

	@Bean
	public LdapUserDetailsContextMapper ldapContextMapper() {
		return new LdapUserDetailsContextMapper(userDetailsService);
	}

	/**
	 * Set up authentication.
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// authentication is always LDAP
		log.info("Enabling LDAP authentication.");
		auth.ldapAuthentication()
				.contextSource(contextSource())
				.userSearchBase(ldapSearchBase)
				.userSearchFilter(ldapSearchFilter)
				.ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
				.userDetailsContextMapper(ldapContextMapper());
	}

	/**
	 * Set up basic authentication and restrict requests based on HTTP methods, URLS, and roles.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.and()
				.csrf().disable() // TODO: Figure out
				.formLogin()
				.permitAll()
				.successHandler(authSuccessHandler)
				.failureHandler(authFailureHandler)
				.and()
				.logout()
				.permitAll()
				.logoutSuccessHandler(logoutSuccessHandler)
				.and()
				.authorizeRequests()
				.antMatchers("/index.html", "/login/**", "/login*", "/login*/**","/", "/assets/**", "/home/**", "/components/**", "/fonts/**")
				.permitAll()
				.antMatchers(HttpMethod.POST).access("hasRole('ADMIN') or hasRole('USER')")
				.antMatchers(HttpMethod.PUT).access("hasRole('ADMIN') or hasRole('USER')")
				.antMatchers(HttpMethod.PATCH).access("hasRole('ADMIN') or hasRole('USER')")
				.antMatchers(HttpMethod.DELETE).denyAll()
				.anyRequest().authenticated();
	}

}
```

Configure the Spring Datasource in your Boot application. If using the standard Docker/MySQL setup, start the MySQL container first and create the database and user. Then start up your application and Flyway migrations for the authentication library should create some structure for users and roles. Insert the users/roles relevant for your application.

Once the application has started, you should be able to log in via curl:

```
curl -c /tmp/cookie.txt -XPOST --data "username=xxxxx&password=xxxxx" http://localhost:8080/login
```

TODO:
Audit tables/Envers?
Client code? Login page?
CSRF
Add back in table-based options
Make max login attempts optional with a default value of 3