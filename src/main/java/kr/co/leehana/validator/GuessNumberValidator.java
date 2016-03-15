package kr.co.leehana.validator;

import kr.co.leehana.annotation.ValidGuessNumber;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.service.GameRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hana Lee
 * @since 2016-03-14 22:57
 */
@Component
public class GuessNumberValidator extends AbstractNumberValidator implements ConstraintValidator<ValidGuessNumber,
		PlayerDto.Update> {

	@Autowired
	private GameRoomService gameRoomService;

	@Override
	public void initialize(ValidGuessNumber constraintAnnotation) {
		// Do nothing
	}

	@Override
	public boolean isValid(PlayerDto.Update updateDto, ConstraintValidatorContext context) {
		boolean result = true;
		final Long gameRoomId = updateDto.getGameRoomId();
		final String guessNumber = updateDto.getGuessNumber();
		final GameRoom gameRoom = gameRoomService.getById(gameRoomId);

		if (guessNumber == null) {
			result = true;
		} else if (!validate(gameRoom, guessNumber) && StringUtils.isNotBlank(getMessage())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(getMessage()).addConstraintViolation();
			result = false;
		}

		return result;
	}
}
