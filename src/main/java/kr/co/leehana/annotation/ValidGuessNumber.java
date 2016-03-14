package kr.co.leehana.annotation;

import kr.co.leehana.validator.GuessNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hana Lee
 * @since 2016-03-14 22:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GuessNumberValidator.class)
@Documented
public @interface ValidGuessNumber {
	String message() default "Invalid guess number";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
