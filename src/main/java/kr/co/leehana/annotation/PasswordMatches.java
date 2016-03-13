package kr.co.leehana.annotation;

import kr.co.leehana.validator.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hana Lee
 * @since 2016-02-04 11:08
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
	String message() default "Password don't match";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
