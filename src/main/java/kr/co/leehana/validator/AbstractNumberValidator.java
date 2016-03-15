package kr.co.leehana.validator;

import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Setting;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2016-03-14 23:07
 */
public class AbstractNumberValidator {

	@Getter
	private String message;

	/**
	 * 1. 빈값을 입력했는가?
	 * 2. 숫자만 입력했는가?
	 * 3. 중복되지 않은 숫자인가?
	 * 4. 랜덤 숫자 갯수 설정과 같은 자리수인가?
	 * 5. 공백 입력은 자동 제거 처리.
	 *
	 * @param numberString 게임 숫자 스트링
	 * @return 검증 결과를 반환한다
	 */
	protected boolean validate(final GameRoom gameRoom, final String numberString) {
		boolean result = false;

		if (StringUtils.isBlank(numberString)) {
			message = "숫자가 비어 있습니다";
		} else if (isNotNumberString(numberString)) {
			message = "0 ~ 9 사이의 숫자만 입력 가능합니다";
		} else if (duplicatedNumbers(numberString)) {
			message = "중복된 숫자는 입력 할 수 없습니다.";
		} else if (isNotCorrectNumberLength(gameRoom, numberString)) {
			message = "숫자의 갯수를 확인해주세요";
		} else {
			result = true;
		}
		return result;
	}

	protected boolean isNotCorrectNumberLength(final GameRoom gameRoom, String numberString) {
		final Setting setting = gameRoom.getSetting();
		boolean result = true;
		if (numberString.length() == setting.getGenerationNumberCount()) {
			result = false;
		}

		return result;
	}

	protected boolean isNotNumberString(String numberString) {
		boolean result = false;
		for (Character ch : numberString.toCharArray()) {
			if (ch < '0' || ch > '9') {
				result = true;
			}

			if (result) {
				break;
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
