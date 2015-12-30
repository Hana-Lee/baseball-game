package com.eyeq.jhs.controller;

import com.eyeq.jhs.model.Ball;
import com.eyeq.jhs.model.ErrorMessage;
import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.model.Setting;
import com.eyeq.jhs.model.Solve;
import com.eyeq.jhs.model.Strike;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;

import java.util.HashSet;
import java.util.Set;

public class GameController {

	private String generateNum;
	private int nthGame = 0;
	private GenerationNumberStrategy strategy;

	public GameController(GenerationNumberStrategy strategy) {
		this.strategy = strategy;
	}

	public void userInputValidation(String inputNumber, Setting setting) {
		if (inputNumber == null || inputNumber.isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (inputNumber.length() != setting.getGenerationNumberCount()) {
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
	public Result checkNumber(String generatedNumber, String inputNum) {
		int strike = 0;
		int ball = 0;

		nthGame++;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (generatedNumber.substring(i, i + 1).equals(inputNum.substring(j, j + 1))) {
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

	public void generatedNumbersValidator(String numbers, ErrorMessage errorMessage) {
		Set<Character> characters = new HashSet<>(numbers.length());
		for (Character ch : numbers.toCharArray()) {
			if (ch < '0' || ch > '9') {
				errorMessage.setMessage("0 ~ 9 사이의 숫자만 입력 가능합니다");
				break;
			}
			characters.add(ch);
		}

		if (characters.size() < numbers.length()) {
			errorMessage.setMessage("중복된 숫자는 입력 할 수 없습니다.");
		}
	}

	public String generateNumber() {
		return strategy.generateNumber();
	}
}