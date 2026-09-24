package org.octri.authentication.test;

import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.validation.ValidLdapEmailDomainValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Helper for building a {@link Validator} capable of constructing {@link ValidLdapEmailDomainValidator}, which requires
 * constructor arguments that the default {@link ConstraintValidatorFactory} cannot supply.
 */
public class ValidatorTestUtil {

	public static Validator buildValidator() {
		ValidatorFactory factory = Validation.byDefaultProvider().configure()
				.constraintValidatorFactory(new ConstraintValidatorFactory() {

					@Override
					@SuppressWarnings("unchecked")
					public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
						if (key == ValidLdapEmailDomainValidator.class) {
							return (T) new ValidLdapEmailDomainValidator(new LdapContextProperties());
						}
						try {
							return key.getDeclaredConstructor().newInstance();
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void releaseInstance(ConstraintValidator<?, ?> instance) {
						// No-op
					}
				})
				.buildValidatorFactory();
		return factory.getValidator();
	}

}
