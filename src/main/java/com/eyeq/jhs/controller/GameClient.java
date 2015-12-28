package com.eyeq.jhs.controller;

import com.eyeq.jhs.model.ErrorMessage;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.ResultDto;
import com.eyeq.jhs.model.Role;
import com.eyeq.jhs.model.User;
import com.eyeq.jhs.type.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameClient {

	private ObjectMapper objectMapper = new ObjectMapper();
	private final ClientBackground client = new ClientBackground();

	public GameClient() {
		client.connect();
	}

	private void runningGame() {
		client.sendSocketData("START");
		boolean isGameOver = false;
		while (!isGameOver) {
			System.out.print("숫자를 입력해주세요 :  ");
			Scanner s2 = new Scanner(System.in);
			if (s2.hasNextLine()) {
				final String inputNum = s2.nextLine();
				client.sendSocketData("GUESS_NUM," + inputNum);

				final String serverMsg = client.getServerMessage();

				try {
					final ResultDto resultDto = objectMapper.readValue(serverMsg, ResultDto.class);
					System.out.println("Result : " + resultDto);

					if (resultDto.getErrorMessage() != null && resultDto.getErrorMessage().getType() != null) {
						System.out.println("오류 메세지 : " + resultDto.getErrorMessage().getType().getMessage());
					} else {
						final int strikeCount = resultDto.getResult().getStrike().getValue();
						final int ballCount = resultDto.getResult().getBall().getValue();
						System.out.println(strikeCount + "스트라이크, " + ballCount + "볼 입니다.");
					}

					if (resultDto.getResult().getSolve().isValue()) {
						System.out.println("축하합니다. 숫자를 맞추셨네요 ^^");
						System.out.println("점수는 : " + resultDto.getScore().getValue() + "점 입니다.");
						isGameOver = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean joiningGameRoom(long gameRoomNum, User user) throws IOException {
		client.sendSocketData("JOIN," + gameRoomNum + ":USER:" + user.getUserId() + ":ROLE:" +
				user.getRole().getRoleType().name());
		final String errorMessageJson = client.getServerMessage();
		final ErrorMessage errorMessage = objectMapper.readValue(errorMessageJson, ErrorMessage.class);

		boolean userIdValid;
		if (errorMessage != null && errorMessage.getType() != null) {
			userIdValid = false;
			System.out.println(errorMessage.getType().getMessage());
		} else {
			userIdValid = true;
		}

		return userIdValid;
	}

	private void joinGameRoom(long gameRoomNum, User user) throws IOException {
		System.out.println("안녕하세요 " + user.getUserId() + "님, " + gameRoomNum + "번 방에 입장하셨습니다");

		boolean gameRoomLeft = false;
		while (!gameRoomLeft) {
			client.sendSocketData("GET_ROOM_LIST");
			final List<GameRoom> gameRoomList = getGameRoomList();

			final GameRoom joinedGameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomNum).collect
					(Collectors.toList()).get(0);
			System.out.println("----- 게임룸 (" + joinedGameRoom.getName() + ") -----");
			System.out.println("접속 유저 : " + joinedGameRoom.getUsers().stream().map(User::getUserId).collect(Collectors
					.joining(", ")));
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
						runningGame();
						break;
					case 2:
						showingGameRoomMenu();
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

	private List<GameRoom> getGameRoomList() throws IOException {
		return objectMapper.readValue(client.getServerMessage(), objectMapper.getTypeFactory().constructCollectionType
				(List.class, GameRoom.class));
	}

	private void showingGameRoomMenu() {
		boolean menuLeft = false;
		while (!menuLeft) {
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
						}
						break;
					case 2:
						System.out.print("값을 입력해주세요. : ");
						Scanner inputSettingNum2 = new Scanner(System.in);
						if (inputSettingNum2.hasNextInt()) {
						}
						break;
					case 0:
						menuLeft = true;
				}
			}

		}
	}

	public void startGame() throws IOException {
		Boolean gameTerminated = false;

		System.out.println("====== 야구게임을 시작합니다 ======");
		while (!gameTerminated) {
			client.sendSocketData("CONNECTION");
			System.out.println("*** 게임룸 리스트 ***");
			client.sendSocketData("GET_ROOM_LIST");
			final List<GameRoom> gameRoomList = getGameRoomList();
			for (GameRoom gameRoom : gameRoomList) {
				System.out.println(gameRoom.getId() + " : " + gameRoom.getName() + " (" + gameRoom.getUsers().size() +
						"/" + gameRoom.getLimit() + ")");
			}
			System.out.println("******************");
			System.out.println();
			System.out.println("====== 게임 메뉴 ======");
			System.out.println("1. 게임룸 생성");
			if (gameRoomList.size() > 0) {
				System.out.println("2. 게임룸 선택");
			}
			System.out.println("0. 종료");
			System.out.println("=====================");
			System.out.print("메뉴를 선택해 주세요 : ");
			Scanner s = new Scanner(System.in);
			if (s.hasNextLine()) {
				switch (s.nextInt()) {
					case 1:
						System.out.println("게임룸 이름을 입력해주세요 : ");
						Scanner gameRoomNameScanner = new Scanner(System.in);
						if (gameRoomNameScanner.hasNextLine()) {
							final String gameRoomName = gameRoomNameScanner.nextLine();
							client.sendSocketData("CREATE_ROOM," + gameRoomName);
							final String createdRoomJson = client.getServerMessage();
							final GameRoom createdGameRoom = objectMapper.readValue(createdRoomJson, GameRoom.class);

							boolean joinCompleted = false;
							User userInfo = null;
							while (!joinCompleted) {
								userInfo = makeUserInfo();
								if (userInfo != null) {
									joinCompleted = joiningGameRoom(createdGameRoom.getId(), userInfo);
								} else {
									System.out.println("유저 정보가 없습니다. 다시 확인해주세요.");
								}
							}

							joinGameRoom(createdGameRoom.getId(), userInfo);
						}
						break;
					case 2:
						System.out.println("게임룸 번호 선택 : ");
						Scanner gameRoomNumScanner = new Scanner(System.in);
						if (gameRoomNumScanner.hasNextLine()) {
							final long gameRoomNum = Long.valueOf(gameRoomNumScanner.nextLine());
							boolean joinCompleted = false;
							User userInfo = null;
							while (!joinCompleted) {
								userInfo = makeUserInfo();
								if (userInfo != null) {
									joinCompleted = joiningGameRoom(gameRoomNum, userInfo);
								} else {
									System.out.println("유저 정보가 없습니다. 다시 확인해주세요.");
								}
							}

							joinGameRoom(gameRoomNum, userInfo);
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

	private User makeUserInfo() throws IOException {
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

			return new User(userId, new Role(RoleType.valueOf(userRole)));
		}

		return null;
	}
}