package org.octri.authentication.validation;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.server.security.SecurityHelper;
import org.octri.authentication.server.security.entity.AuthenticationMethod;
import org.octri.authentication.server.security.entity.User;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that {@link User} entities annotated with {@link ValidLdapEmailDomain} have an email address with the
 * expected domain name when the authentication method is {@link AuthenticationMethod#LDAP}.
 */
public class ValidLdapEmailDomainValidator implements ConstraintValidator<ValidLdapEmailDomain, User> {

	private final LdapContextProperties ldapContextProperties;

	/**
	 * Constructor.
	 *
	 * @param ldapContextProperties
	 *            LDAP configuration
	 */
	public ValidLdapEmailDomainValidator(LdapContextProperties ldapContextProperties) {
		this.ldapContextProperties = ldapContextProperties;
	}

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		if (!hasValidEmailDomain(user)) {
			// while this is a cross-field class validation, the violation only applies to the {@code email} property
			context.disableDefaultConstraintViolation();
			var unwrappedContext = context.unwrap(HibernateConstraintValidatorContext.class);
			unwrappedContext
					.addMessageParameter("domain", "@" + ldapContextProperties.getEmailDomain())
					.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("email")
					.addConstraintViolation();

			return false;
		}

		return true;
	}

	private boolean needsDomainCheck(User user) {
		return user != null && AuthenticationMethod.LDAP.equals(user.getAuthenticationMethod())
				&& !StringUtils.isBlank(user.getEmail())
				&& !StringUtils.isBlank(ldapContextProperties.getEmailDomain());
	}

	private boolean hasValidEmailDomain(User user) {
		if (needsDomainCheck(user)) {
			return SecurityHelper.hasEmailDomain(user, ldapContextProperties.getEmailDomain());
		}

		return true;
	}

}
