package kr.co.leehana.validator;

import kr.co.leehana.annotation.ValidGameNumber;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.model.GameNumber;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.service.GameRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hana Lee
 * @since 2016-03-13 00:48
 */
@Component
public class GameNumberValidator extends AbstractNumberValidator implements ConstraintValidator<ValidGameNumber,
		GameRoomDto.GameNumber> {

	@Autowired
	private GameRoomService gameRoomService;

	@Override
	public void initialize(ValidGameNumber constraintAnnotation) {
		// Do nothing
	}

	@Override
	public boolean isValid(GameRoomDto.GameNumber gameNumberDto, ConstraintValidatorContext context) {
		boolean result = true;
		final Long gameRoomId = gameNumberDto.getGameRoomId();
		final GameNumber gameNumber = gameNumberDto.getNumber();
		final GameRoom gameRoom = gameRoomService.getById(gameRoomId);

		if (gameNumber == null) {
			result = true;
		} else if (!validate(gameRoom, gameNumber.getValue()) && StringUtils.isNotBlank(getMessage())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(getMessage()).addConstraintViolation();
			result = false;
		}

		return result;
	}


}
