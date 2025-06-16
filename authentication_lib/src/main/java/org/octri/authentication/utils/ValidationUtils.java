package org.octri.authentication.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;

/**
 * Validation utilities. Autowire this component to use.
 *
 * @param <T>
 *            bean to be validated
 */
@Component
public class ValidationUtils<T> {

	/**
	 * Regex used to validate email addresses.
	 *
	 * @see <a href=
	 *      "https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/email#Basic_validation">https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/email#Basic_validation</a>
	 */
	public static final String VALID_EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

	/**
	 * Get list of {@link FieldError} used in mustache templates.
	 *
	 * @param object
	 *            the bean being validated
	 * @param validationResult
	 *            constraint violations encountered during bean validation
	 * @return a list of errors suitable for rendering
	 */
	public List<FieldError> getErrors(T object, Set<ConstraintViolation<T>> validationResult) {
		List<FieldError> errors = new ArrayList<>();
		validationResult.forEach(r -> {
			Spliterator<Node> spliterator = Spliterators.spliteratorUnknownSize(r.getPropertyPath().iterator(),
					Spliterator.NONNULL);
			final Optional<Node> lastNode = StreamSupport.stream(spliterator, false).reduce((a, b) -> b);
			if (lastNode.isPresent()) {
				errors.add(new FieldError(object.getClass().getName(), lastNode.get().getName(), r.getMessage()));
			}
		});
		return errors;
	}
}