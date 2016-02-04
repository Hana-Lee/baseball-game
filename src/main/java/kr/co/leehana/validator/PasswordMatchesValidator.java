package kr.co.leehana.validator;

import kr.co.leehana.annotation.PasswordMatches;
import kr.co.leehana.dto.PlayerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hana Lee
 * @since 2016-02-04 11:11
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches constraintAnnotation) {
		// Do nothing
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		PlayerDto.Create createDto = (PlayerDto.Create) value;
		return createDto.getPassword().equals(createDto.getMatchingPassword());
	}
}
