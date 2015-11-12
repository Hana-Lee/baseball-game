package com.eyeq.lhn;

import com.eyeq.lhn.model.Ball;
import com.eyeq.lhn.model.Strike;

/**
 * @author Hana Lee
 * @since 2015-11-11 20-47
 */
public class BaseballGame {

	private String generatedNumber;
	private GameNumberGenerator gameNumberGenerator;

	public static void main(String[] args) {

	}

	public void setGameNumberGenerator(GameNumberGenerator gameNumberGenerator) {
		this.gameNumberGenerator = gameNumberGenerator;
	}

	public void generateNumber() {
		this.generatedNumber = gameNumberGenerator.generate();
	}

	public GuessResult guess(String guessNumbers) {
		assertGuessNumbersValid(guessNumbers);

		if (solved(guessNumbers)) {
			return createSolvedResult();
		} else {
			return createNonSolvedResult(guessNumbers);
		}
	}

	private void assertGuessNumbersValid(String guessNumbers) {
		if (guessNumbers == null) {
			throw new IllegalArgumentException("입력 값이 null 입니다.");
		}

		if (guessNumbers.length() < 3) {
			throw new IllegalArgumentException("입력 값이 3보자 작습니다 : " + guessNumbers);
		}

		for (char ch : guessNumbers.toCharArray()) {
			if (ch < '0' || ch > '9') {
				throw new IllegalArgumentException("입력 값이 숫자가 아닙니다 : " + guessNumbers);
			}
		}

		if (guessNumbers.charAt(0) == guessNumbers.charAt(1)
				|| guessNumbers.charAt(0) == guessNumbers.charAt(2)
				|| guessNumbers.charAt(1) == guessNumbers.charAt(2)) {
			throw new IllegalArgumentException("중복 된 입력 값이 있습니다 : " + guessNumbers);
		}
	}

	private boolean solved(String guessNumbers) {
		return guessNumbers.equals(generatedNumber);
	}

	private GuessResult createSolvedResult() {
		return new GuessResult(true, new Strike(3), new Ball(0));
	}

	private GuessResult createNonSolvedResult(String guessNumbers) {
		int strikes = 0;
		int balls = 0;
		for (int i = 0; i < generatedNumber.length(); i++) {
			int idx = generatedNumber.indexOf(guessNumbers.charAt(i));
			if (idx == i) {
				strikes++;
			} else if (idx > -1) {
				balls++;
			}
		}
		return new GuessResult(false, new Strike(strikes), new Ball(balls));
	}
}
