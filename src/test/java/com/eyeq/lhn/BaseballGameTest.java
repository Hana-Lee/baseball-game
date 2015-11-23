package com.eyeq.lhn;

import com.eyeq.lhn.controller.GameNumberGenerator;
import com.eyeq.lhn.controller.GameNumberRandomGenerator;
import com.eyeq.lhn.exception.GameNotEndException;
import com.eyeq.lhn.exception.UserInputOverLimitException;
import com.eyeq.lhn.model.Ball;
import com.eyeq.lhn.model.GuessResult;
import com.eyeq.lhn.model.Score;
import com.eyeq.lhn.model.Strike;
import com.eyeq.lhn.service.MemorySaveScoreService;
import com.eyeq.lhn.service.ScoreService;
import com.eyeq.lhn.setting.GameSetting;
import com.eyeq.lhn.view.ConsoleViewRenderer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Hana Lee
 * @since 2015-11-11 20-48
 */
public class BaseballGameTest {

	private BaseballGame game;
	private ScoreService scoreService;
	private final int userInputCountLimit = 10;

	@Before
	public void setUp() throws Exception {
		scoreService = new MemorySaveScoreService();
		game = new BaseballGame(scoreService, new ConsoleViewRenderer());

		GameSetting setting = new GameSetting();
		setting.setUserInputCountLimit(userInputCountLimit);
		game.setSetting(setting);
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

	// 게임 점수 생성 실패 테스트
	@Test
	public void 게임점수생성실패테스트_GameNotEndException() {
		generateGameNumber("123");
		GuessResult result = game.guess("345");

		try {
			game.score(result);
			fail();
		} catch (GameNotEndException e) {
		}
	}

	// 게임 점수 생성 성공 테스트
	@Test
	public void 게임점수생성성공테스트() {
		generateGameNumber("123");
		GuessResult result = game.guess("123");

		Score score = game.score(result);
		assertNotNull("스코어는 Null 일수 없습니다", score);
		assertEquals("스코어의 ID 는 1L 이여야만 합니다", 1L, score.getId());
		assertEquals("스코어의 결과필드는 true 이여야만 합니다", true, score.isSolved());
		assertEquals("스코어의 이름필드는 No Name 이여야만 합니다", "No Name", score.getName());
		assertEquals("스코어의 점수필드는 1000 이여야만 합니다", 1000, score.getScore());
		assertEquals("스코어의 활성화필드는 true 이여야만 합니다", true, score.isEnabled());
	}

	// 게임 점수 ID 증가 테스트
	@Test
	public void 게임점수ID값증가테스트() {
		game.deleteScore();

		generateGameNumber("123");
		GuessResult result = game.guess("123");
		Score score = game.score(result);
		game.saveScore(score);
		assertEquals("첫번째 score 의 ID 값은 1L 이여야 합니다", 1L, score.getId());

		generateGameNumber("456");
		result = game.guess("456");
		score = game.score(result);
		game.saveScore(score);
		assertEquals("두번째 score 의 ID 값은 2L 이여야 합니다", 2L, score.getId());
	}

	// 게임 점수 계산 테스트
	@Test
	public void 게임점수계산테스트() {
		generateGameNumber("123");
		GuessResult result = game.guess("123");

		Score score = game.score(result);
		assertEquals("첫번째에 맞춘 게임의 점수는 1000 점 이여야 합니다", 1000, score.getScore());

		game.guess("234");
		game.guess("456");
		game.guess("789");
		result = game.guess("123");
		score = game.score(result);
		assertEquals("4번만에 맞춘 게임의 점수는 960점 이여야 합니다", 960, score.getScore());
	}

	// 게임 점수를 파일에 저장하는 기능 테스트
	@Test
	public void 게임점수파일저장테스트() {
		game.deleteScore();

		Score score = new Score(1L, "이하나", 100, true, "1234", true);
		game.saveScore(score);

		List<Score> scoreList = game.loadScores();
		assertNotNull("결과값은 null 일 수 없습니다", scoreList);
		assertEquals("스코어 파일의 크기는 1 이여야만 합니다", 1, scoreList.size());
		assertEquals("스코어의 ID 는 1L 이여야만 합니다", 1L, scoreList.get(0).getId());
		assertEquals("스코어의 결과필드는 true 이여야만 합니다", true, scoreList.get(0).isSolved());
		assertEquals("스코어의 이름필드는 이하나 이여야만 합니다", "이하나", scoreList.get(0).getName());
		assertEquals("스코어의 점수필드는 100 이여야만 합니다", 100, scoreList.get(0).getScore());
		assertEquals("스코어의 활성화필드는 true 이여야만 합니다", true, scoreList.get(0).isEnabled());
		assertEquals("스코어의 생성필드느 1234 이여야만 합니다", "1234", scoreList.get(0).getCreated());
	}

	// 게임 점수 파일 삭제 테스트
	@Test
	public void 게임점수파일삭제테스트() {
		game.deleteScore();

		List<Score> results = game.loadScores();
		assertNull("결과 파일은 null 이어야 합니다", results);
	}

	// score 클래스의 identity 와 equality 테스트
	@Test
	public void testScoreIdentityAndEquality() {
		Score score1 = new Score(1L, "이하나1", 100, true, "1234", true);
		Score score2 = new Score(2L, "이하나2", 101, true, "123", true);
		if (score1 == score2) {
			fail();
		}
		assertNotEquals(score1, score2);

		Score score3 = new Score(3L, "이하나1", 100, true, "1234", true);
		Score score4 = new Score(3L, "이하나2", 101, true, "123", true);
		if (score3 == score4) {
			fail();
		}
		assertEquals(score3, score4);
	}

	// 숫자 맞추기를 실패 했을때 0점 테스트
	@Test
	public void 숫자맞추기실패테스트() {
		generateGameNumber("123");
		GuessResult result = null;
		for (int i = 1; i <= userInputCountLimit; i++) {
			result = game.guess("234");
		}
		Score score = game.score(result);
		assertEquals("숫자를 못맞췄을때는 점수가 0점이여야 한다", 0, score.getScore());
	}

	// 랜덤 하게 생성된 숫자가 설정된 자리수인지 확인하는 테스트
	@Test
	public void 랜덤생성된숫자의자리수확인테스트() {
		GameSetting setting = new GameSetting();
		int generateNumberCount = 10;
		setting.setGenerateNumberCount(generateNumberCount);

		GameNumberGenerator generator = new GameNumberRandomGenerator(setting);
		String generatedNumber = generator.generate();

		assertEquals("랜덤숫자는 " + generatedNumber + "자리여야 합니다", generateNumberCount, generatedNumber.length());
	}

	// 랜덤하게 생성된 숫자에 중복된 숫자가 생성되는지 확인 테스트
	@Test
	public void 랜덤생성숫자중복테스트() {
		GameSetting setting = new GameSetting();
		setting.setGenerateNumberCount(3);

		GameNumberGenerator generator = new GameNumberRandomGenerator(setting);

		for (int i = 0; i < 1000; i++) {
			String generatedNumber = generator.generate();

			assertFalse("랜덤 생성된 숫자에는 중복된 숫자가 있을 수 없습니다 : " + generatedNumber, 중복숫자체크(generatedNumber));
		}
	}

	// 게임 종료 여부 확인 테스트
	@Test
	public void testGameEndWithMatchingNumber() {
		generateGameNumber("123");
		GuessResult result = game.guess("123");
		boolean isGameEnd = game.isGameEnd(result);
		assertTrue("게임 종료값은 true 여야 합니다", isGameEnd);
	}

	// 게임 횟수만큼 입력했을때 게임이 끝나는지 확인 테스트
	@Test
	public void testGameEndWhenUserInputLimitTime() {
		generateGameNumber("123");
		GuessResult result = null;
		for (int i = 1; i <= userInputCountLimit; i++) {
			result = game.guess("456");
			if (i < userInputCountLimit) {
				assertFalse("게임중에는 게임 종료 값이 false 여야 합니다", game.isGameEnd(result));
			}
		}
		boolean isGameEnd = game.isGameEnd(result);
		assertTrue("게임 종료 값은 true 여야 합니다", isGameEnd);
	}

	private boolean 중복숫자체크(String number) {
		if (number.charAt(0) == number.charAt(1) || number.charAt(0) == number.charAt(2) ||
				number.charAt(1) == number.charAt(2)) {
			return true;
		}

		return false;
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