package kr.co.leehana.validator;

import kr.co.leehana.annotation.ValidGameNumber;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.model.GameNumber;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Setting;
import kr.co.leehana.service.GameRoomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2016-03-13 00:48
 */
@Component
public class GameNumberValidator implements ConstraintValidator<ValidGameNumber, GameRoomDto.Ready> {

	@Autowired
	private GameRoomService gameRoomService;

	private String message;

	private Long gameRoomId;

	@Override
	public void initialize(ValidGameNumber constraintAnnotation) {
		// To nothing
	}

	@Override
	public boolean isValid(GameRoomDto.Ready readyDto, ConstraintValidatorContext context) {
		boolean result = true;
		final GameNumber gameNumber = readyDto.getNumber();
		if (gameNumber == null) {
			result = true;
		}

		gameRoomId = readyDto.getGameRoomId();

		if (!validate(gameNumber) && StringUtils.isNotBlank(message)) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			result = false;
		}

		return result;
	}

	/**
	 * 1. 빈값을 입력했는가?
	 * 2. 숫자만 입력했는가?
	 * 3. 중복되지 않은 숫자인가?
	 * 4. 랜덤 숫자 갯수 설정과 같은 자리수인가?
	 * 5. 공백 입력은 자동 제거 처리.
	 *
	 * @param gameNumber 게임 숫자 객체
	 * @return 검증 결과를 반환한다
	 */
	private boolean validate(GameNumber gameNumber) {
		final String numberString = gameNumber.getValue();
		boolean result = false;

		if (StringUtils.isBlank(numberString)) {
			message = "숫자가 비어 있습니다";
		} else if (isNotNumberString(numberString)) {
			message = "0 ~ 9 사이의 숫자만 입력 가능합니다";
		} else if (duplicatedNumbers(numberString)) {
			message = "중복된 숫자는 입력 할 수 없습니다.";
		} else if (isNotCorrectNumberLength(numberString)) {
			message = "생성할 숫자의 갯수를 확인해주세요";
		} else {
			result = true;
		}
		return result;
	}

	private boolean isNotCorrectNumberLength(String numberString) {
		final GameRoom gameRoom = gameRoomService.getById(gameRoomId);
		final Setting setting = gameRoom.getSetting();
		boolean result = true;
		if (numberString.length() == setting.getGenerationNumberCount()) {
			result = false;
		}

		return result;
	}

	private boolean isNotNumberString(String numberString) {
		boolean result = false;
		for (Character ch : numberString.toCharArray()) {
			if (ch < '0' || ch > '9') {
				message = "0 ~ 9 사이의 숫자만 입력 가능합니다";
				result = true;
			}
		}
		return result;
	}

	private boolean duplicatedNumbers(String numberString) {
		boolean result = false;
		final Set<Character> characters = new HashSet<>(numberString.length());
		for (Character ch : numberString.toCharArray()) {
			characters.add(ch);
		}

		if (characters.size() < numberString.length()) {
			result = true;
		}
		return result;
	}
}
