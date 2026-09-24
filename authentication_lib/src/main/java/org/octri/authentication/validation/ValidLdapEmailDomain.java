package org.octri.authentication.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation that adds {@link ValidLdapEmailDomainValidator} validation to an entity.
 */
@Documented
@Constraint(validatedBy = ValidLdapEmailDomainValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidLdapEmailDomain {

	/**
	 * Default constraint violation message template.
	 * 
	 * @return the default template
	 */
	String message() default "Email domain must be {domain} for LDAP accounts.";

	/**
	 * Validation groups associated with the validation. Defaults to an empty array (default group).
	 * 
	 * @return validation groups
	 */
	Class<?>[] groups() default {};

	/**
	 * Validation payloads associated with the validation. Defaults to an empty array.
	 * 
	 * @return validation payloads
	 */
	Class<? extends Payload>[] payload() default {};

}
