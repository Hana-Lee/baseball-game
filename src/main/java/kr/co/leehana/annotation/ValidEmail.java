package kr.co.leehana.annotation;

import kr.co.leehana.validator.EmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hana Lee
 * @since 2016-02-04 11:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
	String message() default "Invalid email";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
