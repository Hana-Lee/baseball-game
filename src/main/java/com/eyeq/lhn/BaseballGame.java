package com.eyeq.lhn;

import com.eyeq.lhn.controller.GameNumberGenerator;
import com.eyeq.lhn.controller.GameNumberRandomGenerator;
import com.eyeq.lhn.exception.GameNotEndException;
import com.eyeq.lhn.exception.UserInputOverLimitException;
import com.eyeq.lhn.factory.MenuFactory;
import com.eyeq.lhn.model.Ball;
import com.eyeq.lhn.model.GuessResult;
import com.eyeq.lhn.model.Score;
import com.eyeq.lhn.model.Strike;
import com.eyeq.lhn.service.MemorySaveScoreService;
import com.eyeq.lhn.service.ScoreService;
import com.eyeq.lhn.setting.GameSetting;
import com.eyeq.lhn.view.ConsoleViewRenderer;
import com.eyeq.lhn.view.ViewRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Hana Lee
 * @since 2015-11-11 20-47
 */
public class BaseballGame {

	private String generatedNumber;
	private GameNumberGenerator gameNumberGenerator;
	private int guessCount = 0;
	private GameSetting setting;
	private ScoreService scoreService;
	private List<Score> loadedScores;
	private int startScore = 1000;
	private ViewRenderer viewRenderer;
	private boolean gameTerminated = false;

	public static void main(String[] args) {
		BaseballGame baseballGame = new BaseballGame(new MemorySaveScoreService(), new ConsoleViewRenderer());
		GameSetting setting = new GameSetting();
		setting.setGenerateNumberCount(3);
		setting.setUserInputCountLimit(10);
		baseballGame.setGameNumberGenerator(new GameNumberRandomGenerator(setting));
		baseballGame.setSetting(setting);

		baseballGame.start();
	}

	public BaseballGame(ScoreService scoreService, ViewRenderer viewRenderer) {
		this.scoreService = scoreService;
		this.viewRenderer = viewRenderer;

		init();
	}

	private void init() {
		this.loadedScores = loadScores();
		if (loadedScores == null) {
			loadedScores = new ArrayList<>();
		}
	}

	public void start() {
		while (!isGameTerminated()) {
			resetStatus();
			viewRenderer.renderTitle();
			viewRenderer.renderWelcome();
			viewRenderer.renderMenu(MenuFactory.create());
			Scanner s = new Scanner(System.in);
			if (s.hasNextLine()) {
				int userInput = s.nextInt();
				switch (userInput) {
					case 1:
						generateNumber();

						GuessResult result = null;
						while (!isGameEnd(result)) {
							System.out.println("생성된 숫자:" + generatedNumber);
							viewRenderer.renderGameCount(guessCount);
							viewRenderer.renderInputNumberMessage(setting);
							Scanner inputNumber = new Scanner(System.in);
							if (inputNumber.hasNextLine()) {
								result = guess(inputNumber.nextLine());
								viewRenderer.renderGuessResult(result);
							}
						}
						viewRenderer.renderGameEnd(result, guessCount);
						Score score = score(result);
						viewRenderer.renderScore(score);
						viewRenderer.renderInputNameMessage();
						Scanner inputName = new Scanner(System.in);
						if (inputName.hasNext()) {
							score.setName(inputName.next());
						}
						saveScore(score);
						break;
					case 2:
						viewRenderer.renderAllScores(loadedScores);
						break;
					case 3:
						viewRenderer.renderSettingMenu(setting);
						break;
					case 0:
						gameTerminated = true;
						break;
					default:
						break;
				}
			}
		}
	}

	private void resetStatus() {
		this.guessCount = 0;
		this.generatedNumber = "";
		this.startScore = 1000;
	}

	public void setSetting(GameSetting setting) {
		this.setting = setting;
	}

	public void setGameNumberGenerator(GameNumberGenerator gameNumberGenerator) {
		this.gameNumberGenerator = gameNumberGenerator;
	}

	public void generateNumber() {
		this.generatedNumber = gameNumberGenerator.generate();
	}

	public GuessResult guess(String guessNumbers) {
		assertGuessNumbersValid(guessNumbers);

		guessCount++;
		if (guessCount > setting.getUserInputCountLimit()) {
			throw new UserInputOverLimitException();
		}

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

		if (guessNumbers.charAt(0) == guessNumbers.charAt(1) || guessNumbers.charAt(0) == guessNumbers.charAt(2) ||
				guessNumbers.charAt(1) == guessNumbers.charAt(2)) {
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

	public void saveScore(Score score) {
		loadedScores.add(score);
		scoreService.save(loadedScores);
	}

	public List<Score> loadScores() {
		return scoreService.load();
	}

	public void deleteScore() {
		scoreService.delete();

		init();
	}

	public Score score(GuessResult result) {
		if (!result.isSolved() && guessCount < setting.getUserInputCountLimit()) {
			throw new GameNotEndException();
		}
		int score = startScore - ((guessCount - 1) * 10);
		if (guessCount == setting.getUserInputCountLimit() && !result.isSolved()) {
			score = 0;
		}

		return new Score(generateScoreId(), "No Name", score, true, String.valueOf(System.currentTimeMillis()), true);
	}

	private long generateScoreId() {
		if (loadedScores.isEmpty()) {
			return 1L;
		} else {
			Score previousScore = loadedScores.get(loadedScores.size() - 1);
			return previousScore.getId() + 1L;
		}
	}

	public boolean isGameEnd(GuessResult result) {
		return result != null && (result.isSolved() || guessCount == 10);
	}

	public boolean isGameTerminated() {
		return gameTerminated;
	}
}
