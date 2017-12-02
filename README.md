# OCTRI Authentication Library

This library allows you to use the entities and services provided to bootstrap authentication into a new Spring Boot application. Domain classes for users and roles are provided, along with Flyway migrations to initiate the database. Once configured, your application will have endpoints for login and user management. In addition, failure and success handlers are provided that will persist all login attempts with IP addresses for auditing purposes.

## Getting started

The repo 'auth_example_project' in this project shows the minimum configuration needed to use the library. First, add this dependency to your pom:

```
		<dependency>
			<groupId>org.octri.authentication</groupId>
			<artifactId>authentication_lib</artifactId>
			<version>${authentication_lib.version}</version>
		</dependency>
```

The library requires two properties to be set that configures the number of login attempts allowed before a user is locked out. (TODO: This should be optional.) In application.properties set:

```
octri.authentication.max-login-attempts=3
octri.authentication.enable-ldap=true
octri.authentication.enable-table-based=true
```

You can pass these as environment variables as well. 

```
OCTRI_AUTHENTICATION_MAX_LOGIN_ATTEMPTS=3
OCTRI_AUTHENTICATION_ENABLE_LDAP=true
OCTRI_AUTHENTICATION_ENABLE_TABLE_BASED=true
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

The authentication library provides the following `SecurityConfiguration.java`.

```
package org.octri.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.AdminLogoutSuccessHandler;
import org.octri.authentication.server.security.AuthenticationUserDetailsService;
import org.octri.authentication.server.security.JsonResponseAuthenticationFailureHandler;
import org.octri.authentication.server.security.JsonResponseAuthenticationSuccessHandler;
import org.octri.authentication.server.security.LdapUserDetailsContextMapper;
import org.octri.authentication.server.security.StatusOnlyAuthenticationEntryPoint;
import org.octri.authentication.server.security.StatusOnlyLogoutSuccessHandler;
import org.octri.authentication.server.security.TableBasedAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.NullLdapAuthoritiesPopulator;

/**
 * A security configuration class that extends
 * {@link WebSecurityConfigurerAdapter} and sets default annotations for
 * enabling config properties, aspect J auto proxy, JPA auditing, and global
 * method security annotations. It provides LDAP properties and beans for
 * managing authentication.
 *
 * @author sams
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@EnableJpaAuditing
@Configuration
@Order(10)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    protected static final Log log = LogFactory.getLog(SecurityConfiguration.class);

    @Value("${octri.authentication.enable-ldap}")
    protected Boolean enableLdap;

    @Value("${octri.authentication.enable-table-based}")
    protected Boolean enableTableBased;

    @Value("${server.context-path}")
    protected String contextPath;

    @Value("${ldap.contextSource.searchBase}")
    protected String ldapSearchBase;

    @Value("${ldap.contextSource.searchFilter}")
    protected String ldapSearchFilter;

    @Autowired
    protected AuthenticationUserDetailsService userDetailsService;

    @Autowired
    protected StatusOnlyAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    protected JsonResponseAuthenticationSuccessHandler authSuccessHandler;

    @Autowired
    protected JsonResponseAuthenticationFailureHandler authFailureHandler;

    @Autowired
    protected AdminLogoutSuccessHandler adminLogoutSuccessHandler;

    /**
     * It looks like in Spring Web Security 4 there is already an implementation
     * that only returns a status:
     * {@link org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler}
     */
    @Autowired
    protected StatusOnlyLogoutSuccessHandler logoutSuccessHandler;

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

    @Bean
    public TableBasedAuthenticationProvider tableBasedAuthenticationProvider() {
        return new TableBasedAuthenticationProvider(userDetailsService, new BCryptPasswordEncoder());
    }

    /**
     * Set up authentication.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Use table-based authentication by default
        if (enableTableBased) {
			auth.userDetailsService(userDetailsService).and()
					.authenticationProvider(tableBasedAuthenticationProvider());
		}

        // Authentication falls through to LDAP if configured
        if (enableLdap) {
            log.info("Enabling LDAP authentication.");
            auth.ldapAuthentication().contextSource(contextSource()).userSearchBase(ldapSearchBase)
                    .userSearchFilter(ldapSearchFilter).ldapAuthoritiesPopulator(new NullLdapAuthoritiesPopulator())
                    .userDetailsContextMapper(ldapContextMapper());
        } else {
            log.info("Not enabling LDAP authentication: octri.authentication.enable-ldap was false.");
        }
    }

    /**
     * Set up basic authentication and restrict requests based on HTTP methods, URLS, and roles.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .csrf()
                .and()
                .formLogin()
                .permitAll()
                .defaultSuccessUrl("/admin/user/list")
                .failureHandler(authFailureHandler)
                .failureUrl("/error")
                .and()
                .logout()
                .permitAll()
                .logoutSuccessHandler(adminLogoutSuccessHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/index.html","/login/**", "/login*", "/login*/**","/", "/assets/**", "/home/**",
                        "/components/**", "/fonts/**").permitAll()
                .antMatchers(HttpMethod.POST).authenticated()
                .antMatchers(HttpMethod.PUT).authenticated()
                .antMatchers(HttpMethod.PATCH).authenticated()
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
Audit tables for user management
Make max login attempts optional with a default value of 3
