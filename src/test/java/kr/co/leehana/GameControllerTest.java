package kr.co.leehana;

import kr.co.leehana.controller.GameController;
import kr.co.leehana.controller.RandomNumberGenerator;
import kr.co.leehana.model.ErrorMessage;
import kr.co.leehana.model.Result;
import kr.co.leehana.model.Setting;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Hana Lee
 * @since 2015-11-11 20-48
 */
public class GameControllerTest {

	private GameController gameController;

	@Before
	public void setUp() throws Exception {
		gameController = new GameController(setting -> "123");
	}

	// 숫자를 잘못 입력했을때 IllegalArgumentException 이 발생하는지 테스트
	@Test
	public void testGivenInvalidGuessNumber_throwIllegalArgumentException() {
		assertIllegalArgumentException(null, new Setting());
		assertIllegalArgumentException("12", new Setting());

		assertIllegalArgumentException("12a", new Setting());
		assertIllegalArgumentException("a12", new Setting());

		assertIllegalArgumentException("113", new Setting());
		assertIllegalArgumentException("141", new Setting());
		assertIllegalArgumentException("411", new Setting());
		assertIllegalArgumentException("444", new Setting());

		assertIllegalArgumentException("1 2 3", new Setting());
		assertIllegalArgumentException("1 23", new Setting());
		assertIllegalArgumentException("12 3", new Setting());
	}

	// 일부숫자의 값이 맞았을때 게임 룰에 따라 결과값이 제대로 나오는지 확인 하는 테스트
	@Test
	public void testGivenSomeMatchingGuessNumber_returnStrikesAndBalls() {
		assertGuessResult("456", "478", false, 1, 0);
		assertGuessResult("456", "416", false, 2, 0);

		assertGuessResult("456", "123", false, 0, 0);
		assertGuessResult("456", "789", false, 0, 0);
		assertGuessResult("456", "234", false, 0, 1);
		assertGuessResult("456", "567", false, 0, 2);

		assertGuessResult("456", "465", false, 1, 2);
		assertGuessResult("456", "654", false, 1, 2);
		assertGuessResult("456", "645", false, 0, 3);
	}

	// 정확한 숫자를 입력했을때 결과 값으로 Strike 3 과 Resolved true 의 결과가 나오는지 테스트
	@Test
	public void testGivenExactMatchingGuessNumber_returnSolvedResult() {
		assertGuessResult("123", "123", true, 3, 0);

		assertGuessResult("456", "456", true, 3, 0);

		assertGuessResult("789", "789", true, 3, 0);
	}

	// 랜덤 하게 생성된 숫자가 설정된 자리수인지 확인하는 테스트
	@Test
	public void 랜덤생성된숫자의자리수확인테스트() {
		Setting setting = new Setting();
		int generateNumberCount = setting.getGenerationNumberCount();
		RandomNumberGenerator generator = new RandomNumberGenerator();
		String generatedNumber = generator.generate(setting);

		assertEquals("랜덤숫자는 " + generateNumberCount + "자리여야 합니다", generateNumberCount, generatedNumber.length());
	}

	// 랜덤하게 생성된 숫자에 중복된 숫자가 생성되는지 확인 테스트
	@Test
	public void 랜덤생성숫자중복테스트() {
		Setting setting = new Setting();
		setting.setGenerationNumberCount(3);

		RandomNumberGenerator generator = new RandomNumberGenerator();

		for (int i = 0; i < 1000; i++) {
			String generatedNumber = generator.generate(setting);

			ErrorMessage errorMessage = new ErrorMessage();
			gameController.generatedNumbersValidator(generatedNumber, errorMessage);
			assertFalse("랜덤 생성된 숫자에는 중복된 숫자가 있을 수 없습니다 : " + generatedNumber, errorMessage.getMessage() != null &&
					!errorMessage.getMessage().isEmpty());
		}
	}

	// 게임 종료 여부 확인 테스트
	@Test
	public void testGameEndWithMatchingNumber() {
		Result result = gameController.compareNumber("123", "123");
		boolean isGameEnd = gameController.isGameEnd(result);
		assertTrue("게임 종료값은 true 여야 합니다", isGameEnd);
	}

	private void assertIllegalArgumentException(String guessNumbers, Setting setting) {
		try {
			gameController.userInputValidation(guessNumbers, setting);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private void assertGuessResult(String generatedNumber, String guessNumbers, boolean solved, int expectedStrikes,
	                               int expectedBalls) {
		Result guessResult = gameController.compareNumber(generatedNumber, guessNumbers);
		assertThat(guessResult.getSettlement().isSolved(), equalTo(solved));
		assertThat(guessResult.getStrike().getValue(), equalTo(expectedStrikes));
		assertThat(guessResult.getBall().getValue(), equalTo(expectedBalls));
	}
}