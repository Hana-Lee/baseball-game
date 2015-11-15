package com.eyeq.lhn;

import com.eyeq.lhn.exception.UserInputOverLimitException;
import com.eyeq.lhn.model.Ball;
import com.eyeq.lhn.model.Strike;
import com.eyeq.lhn.setting.GameSetting;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Hana Lee
 * @since 2015-11-11 20-48
 */
public class BaseballGameTest {

	private BaseballGame game;

	@Before
	public void setUp() throws Exception {
		game = new BaseballGame();
	}

	// 숫자를 잘못 입력했을때 IllegalArgumentException 이 발생하는지 테스트
	@Test
	public void testGivenInvalidGuessNumber_throwIllegalArgumentException() {
		assertIllegalArgumentException(null);
		assertIllegalArgumentException("12");

		assertIllegalArgumentException("12a");
		assertIllegalArgumentException("a12");

		assertIllegalArgumentException("113");
		assertIllegalArgumentException("141");
		assertIllegalArgumentException("411");
		assertIllegalArgumentException("444");
	}

	// 일부숫자의 값이 맞았을때 게임 룰에 따라 결과값이 제대로 나오는지 확인 하는 테스트
	@Test
	public void testGivenSomeMatchingGuessNumber_returnStrikesAndBalls() {
		generateGameNumber("456");

		assertGuessResult("478", false, 1, 0);
		assertGuessResult("416", false, 2, 0);

		assertGuessResult("123", false, 0, 0);
		assertGuessResult("789", false, 0, 0);
		assertGuessResult("234", false, 0, 1);
		assertGuessResult("567", false, 0, 2);

		assertGuessResult("465", false, 1, 2);
		assertGuessResult("654", false, 1, 2);
		assertGuessResult("645", false, 0, 3);
	}

	// 정확한 숫자를 입력했을때 결과 값으로 Strike 3 과 Resolved true 의 결과가 나오는지 테스트
	@Test
	public void testGivenExactMatchingGuessNumber_returnSolvedResult() {
		generateGameNumber("123");
		assertGuessResult("123", true, 3, 0);

		generateGameNumber("456");
		assertGuessResult("456", true, 3, 0);

		generateGameNumber("789");
		assertGuessResult("789", true, 3, 0);
	}

	// 입력 횟수 제한 초과 테스트
	@Test
	public void 입력횟수제한초과테스트_UserInputOverLimitException() {
		GameSetting setting = new GameSetting();
		setting.setUserInputCountLimit(10);
		game.setSetting(setting);

		generateGameNumber("123");
		for (int i = 1; i <= 11; i++) {
			try {
				game.guess("123");
			} catch (UserInputOverLimitException e) {
				break;
			}
			if (i == 11) {
				fail("입력 횟수 제한 초과 테스트에 실패 하였습니다.");
			}
		}
	}

	private void generateGameNumber(String generatedNumber) {
		game.setGameNumberGenerator(() -> generatedNumber);
		game.generateNumber();
	}

	private void assertIllegalArgumentException(String guessNumbers) {
		try {
			game.guess(guessNumbers);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private void assertGuessResult(String guessNumbers, boolean solved, int expectedStrikes, int expectedBalls) {
		GuessResult guessResult = game.guess(guessNumbers);
		assertThat(guessResult.isSolved(), equalTo(solved));
		assertThat(guessResult.getStrike(), equalTo(new Strike(expectedStrikes).getCount()));
		assertThat(guessResult.getBall(), equalTo(new Ball(expectedBalls).getCount()));
	}
}