package com.eyeq.jhs;

import com.eyeq.jhs.model.Setting;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;
import com.eyeq.jhs.strategy.RandomNumberGenerator;

import java.util.Scanner;

public class App {

	public static void main(String[] args) {
		Setting setting = new Setting();
		BaseballGameEngine bbGame = new BaseballGameEngine(setting);
		GenerationNumberStrategy strtg = new RandomNumberGenerator();
		bbGame.setStrategy(strtg);

		bbGame.startGame();
	}

	public void startGame() {
		Boolean gameTerminated = false;
		String inputNum = "";
		// BaseballGameServer server = new BaseballGameServer();
		BaseballGameClient client = new BaseballGameClient();

		client.connect();

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
					client.sendSocketData("start");
					
					System.out.print("숫자를 입력해주세요 :  ");
					Scanner s2 = new Scanner(System.in);
					if (s2.hasNextLine()) {
						inputNum = s2.nextLine();
						client.sendSocketData(inputNum);
					}
					
					System.out.println(client.getServerMessage());

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
									//setting.setLimitInputWrongNum(inputSettingNum.nextInt());
								}
								break;
							case 2:
								System.out.print("값을 입력해주세요. : ");
								Scanner inputSettingNum2 = new Scanner(System.in);
								if (inputSettingNum2.hasNextInt()) {
									//setting.setNumberOfInputNum(inputSettingNum2.nextInt());
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
					client.closeConnection();
					break;

				}

			}

		}

	}
}
