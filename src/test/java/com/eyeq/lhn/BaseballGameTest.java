package com.eyeq.lhn;

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

	@Test
	public void testGivenExactMatchingGuessNumber_returnSolvedResult() {
		generateGameNumber("123");
		assertGuessResult("123", true, 3, 0);

		generateGameNumber("456");
		assertGuessResult("456", true, 3, 0);

		generateGameNumber("789");
		assertGuessResult("789", true, 3, 0);
	}

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

	private void assertGuessResult(String guessNumbers, boolean solved, int strikes, int balls) {
		GuessResult guessResult = game.guess(guessNumbers);
		assertThat(guessResult.isSolved(), equalTo(solved));
		assertThat(guessResult.getStrikes(), equalTo(strikes));
		assertThat(guessResult.getBalls(), equalTo(balls));
	}
}