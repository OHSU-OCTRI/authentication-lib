package org.octri.authentication.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

/**
 * Validation utilities. Autowire this component to use.
 *
 * @param <T>
 *            bean to be validated
 */
@Component
public class ValidationUtils<T> {

	/**
	 * Get list of {@link FieldError} used in mustache templates.
	 *
	 * @param object
	 * @param validationResult
	 * @return
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