package com.eyeq.jhs.controller;

import com.eyeq.jhs.model.ErrorMessage;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.ResultDto;
import com.eyeq.jhs.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameClient {

	private ObjectMapper objectMapper = new ObjectMapper();

	public void startGame() throws IOException {
		Boolean gameTerminated = false;
		final ClientBackground client = new ClientBackground();

		client.connect();

		System.out.println("====== 야구게임을 시작합니다🤗 ======");
		while (!gameTerminated) {
			client.sendSocketData("CONNECTION");
			System.out.println("*** 게임룸 리스트 ***");
			List<GameRoom> gameRoomList = objectMapper.readValue(client.getServerMessage(), objectMapper
					.getTypeFactory().constructCollectionType(List.class, GameRoom.class));
			for (GameRoom gameRoom : gameRoomList) {
				System.out.println(gameRoom.getId() + " : " + gameRoom.getName() + " (" + gameRoom.getUsers().size() +
						")");
			}
			System.out.println("******************");
			System.out.println();
			System.out.println("====== 게임 메뉴 ======");
			System.out.println("1. 게임룸 선택");
			System.out.println("0. 종료");
			System.out.println("=====================");
			System.out.print("메뉴를 선택해 주세요 : ");
			Scanner s = new Scanner(System.in);
			if (s.hasNextLine()) {
				switch (s.nextInt()) {
					case 1:
						System.out.println("게임룸 번호 선택 : ");
						Scanner gameRoomNumScanner = new Scanner(System.in);
						if (gameRoomNumScanner.hasNextLine()) {
							final long gameRoomNum = Long.valueOf(gameRoomNumScanner.nextLine());
							boolean userIdValid = false;
							while (!userIdValid) {
								System.out.println("유저 아이디를 입력해주세요 : ");
								Scanner userNameScanner = new Scanner(System.in);
								if (userNameScanner.hasNextLine()) {
									final String userId = userNameScanner.nextLine();
									System.out.println("1. 공격, 2. 수비 중에 하나를 선택해주세요 :");
									Scanner userRoleScanner = new Scanner(System.in);
									String userRole = "ATTACKER";
									if (userRoleScanner.hasNextLine()) {
										final String userRoleSelect = userRoleScanner.nextLine();
										if (userRoleSelect.equals("2")) {
											userRole = "DEPENDER";
										}
									}

									client.sendSocketData("JOIN," + gameRoomNum + ":USER:" + userId + ":ROLE:" + userRole);
									final String errorMessageJson = client.getServerMessage();
									final ErrorMessage errorMessage = objectMapper.readValue(errorMessageJson,
											ErrorMessage.class);

									if (errorMessage != null && errorMessage.getType() != null) {
										userIdValid = false;
										System.out.println(errorMessage.getType().getMessage());
									} else {
										userIdValid = true;
										System.out.println("안녕하세요 " + userId + "님, " + gameRoomNum + "번 방에 입장하셨습니다");

										boolean gameRoomLeft = false;
										while (!gameRoomLeft) {
											client.sendSocketData("GET_ROOM_LIST");
											gameRoomList = objectMapper.readValue(client.getServerMessage(),
													objectMapper.getTypeFactory().constructCollectionType(List.class,
															GameRoom.class));

											final GameRoom joinedGameRoom = gameRoomList.stream().filter(r -> r.getId
													() == gameRoomNum).collect(Collectors.toList()).get(0);
											System.out.println("----- 게임룸 (" + joinedGameRoom.getName() + ") -----");
											System.out.println("접속 유저 : " + joinedGameRoom.getUsers().stream().map
													(User::getUserId).collect(Collectors.joining(",")));
											System.out.println();
											System.out.println("----- 메뉴 -----");
											System.out.println("1. 준비");
											System.out.println("1. 시작");
											System.out.println("2. 설정");
											System.out.println("0. 나가기");
											System.out.println("---------------");
											System.out.println("메뉴를 선택해 주세요 : ");
											Scanner roomMenuScanner = new Scanner(System.in);
											if (roomMenuScanner.hasNextLine()) {
												final int selectedMenu = Integer.valueOf(roomMenuScanner.nextLine());
												switch (selectedMenu) {
													case 1:
														client.sendSocketData("START");
														boolean isGameOver = false;
														while (!isGameOver) {
															System.out.print("숫자를 입력해주세요 :  ");
															Scanner s2 = new Scanner(System.in);
															if (s2.hasNextLine()) {
																String inputNum = s2.nextLine();
																client.sendSocketData("GUESS_NUM," + inputNum);

																String serverMsg = client.getServerMessage();

																try {
																	ResultDto resultDto = objectMapper.readValue
																			(serverMsg, ResultDto.class);
																	System.out.println("Result : " + resultDto);

																	if (resultDto.getErrorMessage() != null) {
																		System.out.println("오류 메세지 : " + resultDto
																				.getErrorMessage().getType()
																				.getMessage());
																	} else {
																		System.out.println(resultDto.getResult()
																				.getStrike().getValue() + "스트라이크, " +
																				resultDto.getResult().getBall()
																						.getValue() +
																				"볼 입니다.");
																	}

																	if (resultDto.getResult().getSolve().isValue()) {
																		System.out.println("축하합니다. 숫자를 맞추셨네요 ^^");
																		System.out.println("점수는 : " + resultDto
																				.getScore().getValue() + "점 입니다.");
																		isGameOver = true;
																	}
																} catch (IOException e) {
																	e.printStackTrace();
																}
															}
														}
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
																		Scanner inputSettingNum = new Scanner(System
																				.in);
																		if (inputSettingNum.hasNextInt()) {
																			//setting.setLimitWrongInputCount
																			// (inputSettingNum
																			// .nextInt());
																		}
																		break;
																	case 2:
																		System.out.print("값을 입력해주세요. : ");
																		Scanner inputSettingNum2 = new Scanner(System
																				.in);
																		if (inputSettingNum2.hasNextInt()) {
																			//setting.setLimitGuessInputCount
																			// (inputSettingNum2
																			// .nextInt());
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
														gameRoomLeft = true;
														client.closeConnection();
														break;
													default:
														break;
												}
											}
										}
									}
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