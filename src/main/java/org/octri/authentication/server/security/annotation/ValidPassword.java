package org.octri.authentication.server.security.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.octri.authentication.server.security.password.PasswordConstraintValidator;

/**
 * Mark a field for password validation.<br>
 * <br>
 * Provides the following rules:<br>
 * <ul>
 * <li>Must be 8 characters or more
 * <li>Include 1 alphabetical character
 * <li>Include 1 digit
 * <li>Include at least 1 upper case letter, or special character
 * <li>Filters by dictionary matches (password-blacklist.txt)
 * </ul>
 * There are two other rules handled by
 * {@link UserController#changePassword(String, String, String, String, org.springframework.web.servlet.mvc.support.RedirectAttributes, javax.servlet.http.HttpServletRequest, org.springframework.ui.Model)
 * <ul>
 * <li>Prevent using previous password.
 * <li>Prevent using username in the password.
 * </ul>
 *
 * @author sams
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidPassword {

	String message() default "Invalid Password";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}