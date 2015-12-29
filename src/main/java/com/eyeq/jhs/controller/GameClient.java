package com.eyeq.jhs.controller;

import com.eyeq.jhs.model.ErrorMessage;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.ResultDto;
import com.eyeq.jhs.model.Role;
import com.eyeq.jhs.model.Setting;
import com.eyeq.jhs.model.User;
import com.eyeq.jhs.type.RoleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameClient {

	private final ClientBackground client = new ClientBackground();
	private ObjectMapper objectMapper = new ObjectMapper();

	public GameClient() {
		client.connect();
	}

	private void runningGame() throws IOException {
		client.sendSocketData("START");
		boolean isGameOver = false;
		while (!isGameOver) {
			System.out.print("숫자를 입력해주세요 :  ");
			Scanner s2 = new Scanner(System.in);
			if (s2.hasNextLine()) {
				final String inputNum = s2.nextLine();
				client.sendSocketData("GUESS_NUM," + inputNum);

				final String resultDtoJson = client.getServerMessage();

				final ResultDto resultDto = objectMapper.readValue(resultDtoJson, ResultDto.class);
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
			}
		}
	}

	private boolean joiningGameRoom(long gameRoomNum, User user) throws IOException {
		client.sendSocketData("JOIN," + gameRoomNum + ":USER:" + user.getId() + ":ROLE:" +
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
		System.out.println("안녕하세요 " + user.getId() + "님, " + gameRoomNum + "번 방에 입장하셨습니다");

		boolean gameRoomLeft = false;
		while (!gameRoomLeft) {
			client.sendSocketData("GET_ROOM_LIST");
			final List<GameRoom> gameRoomList = getGameRoomList();

			final GameRoom joinedGameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomNum).collect
					(Collectors.toList()).get(0);
			System.out.println("----- 게임룸 (" + joinedGameRoom.getName() + ") -----");
			final String userList = joinedGameRoom.getUsers().stream().map(User::getId).collect(Collectors.joining(", " +
					""));
			System.out.println("접속 유저 : " + userList);
			client.sendSocketData("GET_SETTING," + gameRoomNum);
			final String settingJson = client.getServerMessage();
			final Setting setting = objectMapper.readValue(settingJson, Setting.class);
			System.out.println("** 현재 설정 **");
			System.out.println("* 공격 횟수 " + setting.getLimitGuessInputCount() + "회");
			System.out.println("* 생성 갯수 " + setting.getGenerationNumberCount() + "개");
			System.out.println("* 입력오류제한 : " + setting.getLimitWrongInputCount() + "회");
			System.out.println();
			System.out.println("----- 메뉴 -----");
			if (user.getRole().getRoleType().equals(RoleType.ATTACKER)) {
				System.out.println("1. 준비");
			} else if (user.getRole().getRoleType().equals(RoleType.DEPENDER)) {
				System.out.println("1. 시작");
			}

			if (joinedGameRoom.getOwner().getId().equals(user.getId())) {
				System.out.println("2. 설정");
			}
			System.out.println("0. 게임룸 나가기");
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
						showingGameRoomMenu(gameRoomNum);
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

	private void showingGameRoomMenu(long gameRoomId) throws JsonProcessingException {
		boolean menuLeft = false;
		final Setting setting = new Setting();
		while (!menuLeft) {
			System.out.println("==== 메뉴를 선택해주세요 =======");
			System.out.println("1. 입력 오류 횟수 설정");
			System.out.println("2. 공격 횟수 설정");
			System.out.println("3. 생성 숫자 자리수 설정");
			System.out.println("4. 저장(저장을 하지 않으면 이전 설정유지)");
			System.out.println("0. 메뉴 나가기");
			Scanner settingInput = new Scanner(System.in);
			if (settingInput.hasNextInt()) {
				switch (settingInput.nextInt()) {
					case 1:
						System.out.print("값을 입력해주세요. : ");
						Scanner limitWrongNumSettingScanner = new Scanner(System.in);
						if (limitWrongNumSettingScanner.hasNextInt()) {
							setting.setLimitWrongInputCount(Integer.parseInt(limitWrongNumSettingScanner.nextLine()));
						}
						break;
					case 2:
						System.out.print("값을 입력해주세요. : ");
						Scanner limitGuessNumSettingScanner = new Scanner(System.in);
						if (limitGuessNumSettingScanner.hasNextInt()) {
							setting.setLimitGuessInputCount(Integer.parseInt(limitGuessNumSettingScanner.nextLine()));
						}
						break;
					case 3:
						System.out.print("값을 입력해주세요. : ");
						Scanner generationNumCountSettingScanner = new Scanner(System.in);
						if (generationNumCountSettingScanner.hasNextInt()) {
							setting.setGenerationNumberCount(Integer.parseInt(generationNumCountSettingScanner
									.nextLine()));
						}
						break;
					case 4:
						client.sendSocketData("SET_SETTING," + gameRoomId + ":wrong:" + setting
								.getLimitWrongInputCount() + ":guess:" + setting.getLimitGuessInputCount() + ":count:"
								+ setting.getGenerationNumberCount());
						if (!client.getServerMessage().isEmpty()) {
							System.out.println("저장이 완료 되었습니다.");
						} else {
							System.out.println("저장에 실패 했습니다. 다시 시도 해주세요.");
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
			if (gameRoomList.isEmpty()) {
				System.out.println("생성된 게임룸이 없습니다.");
			} else {
				for (GameRoom gameRoom : gameRoomList) {
					final String roomName = gameRoom.getName();
					final long roomId = gameRoom.getId();
					final int userCountInRoom = gameRoom.getUsers().size();
					final int limitCount = gameRoom.getLimit();
					System.out.println(roomId + " : " + roomName + " (" + userCountInRoom + "/" + limitCount + ")");
				}
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