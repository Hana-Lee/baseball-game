package com.eyeq.jhs;

import com.eyeq.jhs.model.Ball;
import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.model.Solve;
import com.eyeq.jhs.model.Strike;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;

public class BaseballGameEngine {

	private String generateNum;
	private int nthGame = 0;
	private GenerationNumberStrategy strategy;

	public BaseballGameEngine(GenerationNumberStrategy strategy) {
		this.strategy = strategy;
	}

	public void guess(String inputNumber) {
		if (inputNumber == null || inputNumber.isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (inputNumber.length() != 3) {
			throw new IllegalArgumentException();
		}

		if (inputNumber.contains(" ")) {
			throw new IllegalArgumentException();
		}

		for (char ch : inputNumber.toCharArray()) {
			if (ch < '0' || ch > '9') {
				throw new IllegalArgumentException();
			}
		}

		if (inputNumber.charAt(0) == inputNumber.charAt(1) || inputNumber.charAt(1) == inputNumber.charAt(2) ||
				inputNumber.charAt(0) == inputNumber.charAt(2)) {
			throw new IllegalArgumentException();
		}
	}

	// 입력받 숫자와 랜덤하게 생성된 숫자 비교(strike, ball체크)
	public Result checkNumber(String inputNum) {
		int strike = 0;
		int ball = 0;

		nthGame++;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (generateNum.substring(i, i + 1).equals(inputNum.substring(j, j + 1))) {
					if (i == j) {
						strike++;
						break;
					} else {
						ball++;
						break;
					}
				}
			}
		}

		return makeResult(new Strike(strike), new Ball(ball));
	}

	// 게임이 끝나는지 판단(3strikes시 게임 끝)
	private Result makeResult(Strike strike, Ball ball) {
		boolean isSolved;

		if (strike.getValue() == 3) {
			isSolved = true; // 판단하는 객체생성, BaseballGame객체에서 체크.
		} else {
			isSolved = false;
		}
		return new Result(new Solve(isSolved), strike, ball);
	}

	public boolean endingGame(Result result) {
		boolean ending;

		if (result.getSolve().isValue()) {
			ending = true;
		} else if (nthGame > 10) {
			ending = true;
		} else {
			ending = false;
		}

		return ending;
	}

	public boolean isGameOver(Result result) {
		if (result.getSolve().isValue()) {
			return true;
		} else if (nthGame == 10) {
			return true;
		}

		return false;
	}

	public int getNthGame() {
		return nthGame;
	}

	public void generateNum() {
		generateNum = strategy.generateNumber();
	}

	public void resetStatus() {
		nthGame = 0;
	}
}