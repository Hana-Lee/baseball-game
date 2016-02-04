package kr.co.leehana.validator;

import kr.co.leehana.annotation.ValidEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hana Lee
 * @since 2016-02-04 11:27
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, CharSequence> {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
			"@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void initialize(final ValidEmail constraintAnnotation) {
		// Do nothing
	}

	@Override
	public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
		return validateEmail(value);
	}

	private boolean validateEmail(final CharSequence email) {
		final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		final Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}
}
