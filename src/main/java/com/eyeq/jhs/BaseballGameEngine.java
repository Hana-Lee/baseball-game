package com.eyeq.jhs;

import com.eyeq.jhs.model.*;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;

import java.util.Scanner;

public class BaseballGameEngine {

	private String generateNum;
	private int nthGame = 0;
	private int wrongNumber = 0;

	private GenerationNumberStrategy strategy;
	private boolean isGameOver;

	private Setting setting;

	public BaseballGameEngine(Setting setting) {
		this.setting = setting;
	}

	public void startGame() {
		Boolean gameTerminated = false;
		String inputNum = "";
		//BaseballGameServer server = new BaseballGameServer();
		BaseballGameClient client = new BaseballGameClient();
		
		//server.startServer();

		System.out.println("====== 야구게임을 시작합니다🤗 ======");
		while (!gameTerminated) {
			System.out.println("====== 게임 메뉴 ======");
			System.out.println("1. 시작");
			System.out.println("2. Settings");
			System.out.println("0. 종료");
			System.out.println("=====================");
			System.out.print("메뉴를 선택해 주세요 : ");
			Scanner s = new Scanner(System.in);
			if (s.hasNextLine()) {
				switch (s.nextInt()) {
				case 1:
					resetStatus();
					generateNum();

					Result gameResult = null;

					while (wrongNumber < setting.getLimitInputWrongNum()
							&& getNthGame() < setting.getNumberOfInputNum()
							&& !isGameOver) {
						System.out.print(getNthGame() + 1 + "번째 입력입니다. ");
						System.out.println("(0부터 9까지의 숫자로 3자리수를 입력합니다.)");
						// System.out.println("생성된 숫자 : " + generateNum);

						// 잘못된 입력 연속 5번시 게임 종료
						// 잘못입력된 수 제한 setting 설정가능하게끔 리팩토링 예정
						try {
							System.out.print("숫자를 입력해주세요 :  ");
							Scanner s2 = new Scanner(System.in);
							if (s2.hasNextLine()) {
								inputNum = s2.nextLine();
								guess(inputNum);
							}
							gameResult = checkNumber(inputNum);
							client.sendSocketData(inputNum);
							
							System.out.println("** 스트라이크 : "
									+ gameResult.getStrikeCount() + ", 볼 : "
									+ gameResult.getBallsCount());
							if (isGameOver(gameResult)) {
								isGameOver = true;
							}
						} catch (IllegalArgumentException e) {
							System.out.println("잘못된 입력입니다.");
							wrongNumber++;
						}
					}
					System.out.println("게임이 종료되었습니다.");
					System.out.println("게임 점수: "
							+ Score.calculateScore(getNthGame(), gameResult));
					break;
				case 2:
					boolean exit = false;
					while (!exit) {
						System.out.println("==== 메뉴를 선택해주세요 =======");
						System.out.println("1. 잘못된 값 연속 입력 횟수 제한 값 수정");
						System.out.println("2. 수 입력 횟수 제한 값 수정");
						System.out.println("0. 메인메뉴");
						Scanner settingInput = new Scanner(System.in);
						if (settingInput.hasNextInt()) {
							switch (settingInput.nextInt()) {
							case 1:
								System.out.print("값을 입력해주세요. : ");
								Scanner inputSettingNum = new Scanner(System.in);
								if (inputSettingNum.hasNextInt()) {
									setting.setLimitInputWrongNum(inputSettingNum
											.nextInt());
								}
								break;
							case 2:
								System.out.print("값을 입력해주세요. : ");
								Scanner inputSettingNum2 = new Scanner(
										System.in);
								if (inputSettingNum2.hasNextInt()) {
									setting.setNumberOfInputNum(inputSettingNum2
											.nextInt());
								}
								break;
							case 0:
								// settingInput.close();
								exit = true;
							}
						}

					}
					break;
				case 0:
					System.out.println("안녕히가세요");
					gameTerminated = true;
					client.sendSocketData("EXIT");
					break;

				}

			}

		}
	}

	public void initialize() {

	}

	public void setStrategy(GenerationNumberStrategy strategy) {
		this.strategy = strategy;
	}

	public void guess(String inputNumber) {
		if (inputNumber == null || inputNumber.isEmpty() || inputNumber == "") {
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

		if (inputNumber.charAt(0) == inputNumber.charAt(1)
				|| inputNumber.charAt(1) == inputNumber.charAt(2)
				|| inputNumber.charAt(0) == inputNumber.charAt(2)) {
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
				Boolean result = generateNum.substring(i, i + 1).equals(
						inputNum.substring(j, j + 1));
				if (result == true) {
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

		if (strike.getCount() == 3) {
			isSolved = true; // 판단하는 객체생성, BaseballGame객체에서 체크.
		} else {
			isSolved = false;
		}
		return new Result(isSolved, strike, ball);
	}

	public boolean endingGame(Result result) {
		boolean ending;

		if (result.isSolved() == true) {
			ending = true;
		} else if (nthGame > 10) {
			ending = true;
		} else {
			ending = false;
		}

		return ending;
	}

	public boolean isGameOver(Result result) {
		if (result.isSolved()) {
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
		wrongNumber = 0;
		isGameOver = false;

	}

}