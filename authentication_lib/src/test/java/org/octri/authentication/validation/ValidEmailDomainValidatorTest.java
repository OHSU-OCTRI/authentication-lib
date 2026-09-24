package org.octri.authentication.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.server.security.entity.AuthenticationMethod;
import org.octri.authentication.server.security.entity.User;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

@ExtendWith(MockitoExtension.class)
public class ValidEmailDomainValidatorTest {

	private static final String EMAIL_DOMAIN = "example.com";

	@Mock
	private ConstraintValidatorContext context;

	private LdapContextProperties ldapContextProperties;
	private ValidLdapEmailDomainValidator validator;
	private User user;

	@BeforeEach
	public void setUp() {
		ldapContextProperties = new LdapContextProperties();
		ldapContextProperties.setEmailDomain(EMAIL_DOMAIN);
		validator = new ValidLdapEmailDomainValidator(ldapContextProperties);

		user = new User();
		user.setAuthenticationMethod(AuthenticationMethod.LDAP);
		user.setEmail("foo@" + EMAIL_DOMAIN);
	}

	@Test
	public void validForLdapUserWithMatchingDomain() {
		assertTrue(validator.isValid(user, context), "Email matching the configured domain should be valid");
	}

	@Test
	public void invalidForLdapUserWithMismatchedDomain() {
		user.setEmail("foo@other.com");
		mockConstraintViolationBuilder();

		assertFalse(validator.isValid(user, context), "Email not matching the configured domain should be invalid");
	}

	@Test
	public void invalidLdapUserBuildsConstraintViolationWithDomainParameter() {
		user.setEmail("foo@other.com");
		HibernateConstraintValidatorContext unwrappedContext = mockConstraintViolationBuilder();

		validator.isValid(user, context);

		verify(unwrappedContext).addMessageParameter("domain", "@" + EMAIL_DOMAIN);
	}

	@Test
	public void validForTableBasedUserRegardlessOfEmailDomain() {
		user.setAuthenticationMethod(AuthenticationMethod.TABLE_BASED);
		user.setEmail("foo@other.com");

		assertTrue(validator.isValid(user, context), "Table-based users should not be checked against the LDAP domain");
	}

	@Test
	public void validForLdapUserWithNullEmail() {
		user.setEmail(null);

		assertTrue(validator.isValid(user, context), "A null email should not fail domain validation");
	}

	@Test
	public void validForNullUser() {
		assertTrue(validator.isValid(null, context), "A null user should not fail domain validation");
	}

	@Test
	public void validWhenConfiguredEmailDomainIsNull() {
		ldapContextProperties.setEmailDomain(null);
		user.setEmail("foo@other.com");

		assertTrue(validator.isValid(user, context), "A null configured email domain should skip validation");
	}

	private HibernateConstraintValidatorContext mockConstraintViolationBuilder() {
		HibernateConstraintValidatorContext unwrappedContext = mock(HibernateConstraintValidatorContext.class);
		HibernateConstraintViolationBuilder builder = mock(HibernateConstraintViolationBuilder.class);
		NodeBuilderCustomizableContext nodeBuilder = mock(NodeBuilderCustomizableContext.class);

		when(context.unwrap(HibernateConstraintValidatorContext.class)).thenReturn(unwrappedContext);
		when(unwrappedContext.addMessageParameter("domain", "@" + EMAIL_DOMAIN)).thenReturn(unwrappedContext);
		when(context.getDefaultConstraintMessageTemplate())
				.thenReturn("Email domain must be {domain} for LDAP accounts.");
		when(unwrappedContext.buildConstraintViolationWithTemplate("Email domain must be {domain} for LDAP accounts."))
				.thenReturn(builder);
		when(builder.addPropertyNode("email")).thenReturn(nodeBuilder);
		when(nodeBuilder.addConstraintViolation()).thenReturn(context);

		return unwrappedContext;
	}

}
