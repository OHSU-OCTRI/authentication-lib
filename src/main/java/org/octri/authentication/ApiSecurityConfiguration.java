package org.octri.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * This class handles all security, AspectJ, and JPA auditing configuration. It extends Spring's
 * {@link WebSecurityConfigurerAdapter} to easily create a
 * {@link org.springframework.security.config.annotation.web.WebSecurityConfigurer} instance.
 * @author sams
 */
@Configuration
@Order(20)
public class ApiSecurityConfiguration extends AbstractSecurityConfiguration {

	/**
	 * Set up authentication.
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		authentication(auth);
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
				.antMatchers("/index.html","/login/**", "/login*", "/login*/**","/", "/assets/**", "/home/**",
						"/components/**", "/fonts/**").permitAll()
				.antMatchers(HttpMethod.POST).access(MethodSecurityExpressions.USER_OR_ADMIN_OR_SUPER)
				.antMatchers(HttpMethod.PUT).access(MethodSecurityExpressions.USER_OR_ADMIN_OR_SUPER)
				.antMatchers(HttpMethod.PATCH).access(MethodSecurityExpressions.USER_OR_ADMIN_OR_SUPER)
				.antMatchers(HttpMethod.DELETE).denyAll()
				.anyRequest().authenticated();
	}

}
