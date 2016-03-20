package kr.co.leehana.controller;

import kr.co.leehana.model.Ball;
import kr.co.leehana.model.ErrorMessage;
import kr.co.leehana.model.GuessNumberComparedResult;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import kr.co.leehana.model.Settlement;
import kr.co.leehana.model.Strike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GameController {

	@Autowired
	private GenerationNumberStrategy generationNumberStrategy;

	public GameController(GenerationNumberStrategy generationNumberStrategy) {
		this.generationNumberStrategy = generationNumberStrategy;
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

	public GuessNumberComparedResult compareNumber(String generatedNumbers, String guessNumbers) {
		int strikes = 0;
		int balls = 0;
		int genNumCount = generatedNumbers.length();
		for (int i = 0; i < genNumCount; i++) {
			int idx = generatedNumbers.indexOf(guessNumbers.charAt(i));
			if (idx == i) {
				strikes++;
			} else if (idx > -1) {
				balls++;
			}
		}

		return new GuessNumberComparedResult(new Settlement(isSolved(genNumCount, strikes)), new Strike(strikes), new
				Ball(balls));
	}

	private boolean isSolved(int generatedNumberCount, int strikes) {
		return strikes == generatedNumberCount;
	}

	public String makeGuessResultMessage(final Player player) {
		final String message = "[" + player.getGuessNumber() + "] - ";
		final GuessNumberComparedResult result =  player.getResult();
		if (isGameOver(result)) {
			return message + "숫자를 맞췄습니다";
		} else {
			return message + result.getStrike().getValue() + "스트라이크, " + result.getBall().getValue() + "볼";
		}
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

	public String generateNumber(Setting setting) {
		return generationNumberStrategy.generateRandomNumber(setting);
	}

	public boolean isGameOver(GuessNumberComparedResult result) {
		return result.getSettlement() != null && result.getSettlement().getSolved();
	}
}