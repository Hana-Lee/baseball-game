package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.model.ErrorMessage;
import kr.co.leehana.model.OldGameRoom;
import kr.co.leehana.model.OldUser;
import kr.co.leehana.model.ResultDto;
import kr.co.leehana.model.Role;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.Setting;
import kr.co.leehana.type.GameRole;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameClient {

	private final ClientBackground client = new ClientBackground();
	private ObjectMapper objectMapper = new ObjectMapper();

	private OldUser user;

	public GameClient() {
		client.connect();
	}

	private void runningGame(long gameRoomId) throws IOException, InterruptedException {
		client.sendSocketData("START," + gameRoomId);
		System.out.println("게임이 시작되었습니다. 행운을 빌어요~");

		boolean isGameOver = false;
		int count = 1;
		while (!isGameOver) {
			System.out.println(count + "번째 턴입니다.");
			System.out.print("숫자를 입력해주세요 : ");
			Scanner s2 = new Scanner(System.in);
			boolean hasErrorMessage = false;
			boolean onlyOneAttacker = false;
			if (s2.hasNextLine()) {
				final String inputNum = s2.nextLine();
				client.sendSocketData("GUESS_NUM," + inputNum + ":ROOM_ID:" + gameRoomId + ":USER_ID:" + user.getEmail());

				final String resultDtoJson = client.getServerMessage();
				System.out.println("result dto json : " + resultDtoJson);
				final ResultDto resultDto = objectMapper.readValue(resultDtoJson, ResultDto.class);
				System.out.println("Result : " + resultDto);

				onlyOneAttacker = resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRole().getRoleType()
						.equals(GameRole.ATTACKER)).count() == 1;

				if (resultDto.getErrorMessage() != null && resultDto.getErrorMessage().getMessage() != null &&
						!resultDto.getErrorMessage().getMessage().isEmpty()) {
					System.out.println("오류 메세지 : " + resultDto.getErrorMessage().getMessage());
					hasErrorMessage = true;
					if (resultDto.getUser().getWrongCount() >= resultDto.getGameRoom().getSetting()
							.getLimitWrongInputCount()) {
						System.out.println("입력 오류 횟수 제한을 초과하여 게임을 종료합니다");
						System.out.println("점수는 : " + resultDto.getScore().getValue() + "점 입니다.");
						isGameOver = true;
					}
				} else {
					final int strikeCount = resultDto.getResult().getStrike().getValue();
					final int ballCount = resultDto.getResult().getBall().getValue();
					System.out.println(strikeCount + "스트라이크, " + ballCount + "볼 입니다.");
				}

				if (resultDto.getResult() != null && resultDto.getResult().getSettlement().isSolved()) {
					System.out.println("축하합니다. 숫자를 맞추셨네요 ^^");
					System.out.println(resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRole().getRoleType
							().equals(GameRole.ATTACKER)).count() + "명의 유저중 " + resultDto.getUser().getRank()
							.getValue() + "등 입니다.");
					System.out.println("점수 : " + resultDto.getScore().getValue() + "점 입니다.");
					System.out.println("누적 점수 : " + resultDto.getUser().getTotalScore().getValue() + "점 입니다.");
					isGameOver = true;
				}
			}

			if (!onlyOneAttacker && !hasErrorMessage && !isGameOver) {
				System.out.println("모든 유저가 입력을 마칠때까지 대기중 입니다");
				boolean allUserCompletedGuess = false;
				while (!allUserCompletedGuess) {
					client.sendSocketData("ALL_USER_COMPLETED_GUESS," + gameRoomId);
					String allUserCompletedGuessJson = client.getServerMessage();
					allUserCompletedGuess = objectMapper.readValue(allUserCompletedGuessJson, Boolean.class);

					Thread.sleep(500);
				}

				client.sendSocketData("ALL_USER_GUESS_COMPLETE_STATE_RESET," + gameRoomId);
				String resetCompleteJson = client.getServerMessage();
				boolean resetCompleted = objectMapper.readValue(resetCompleteJson, Boolean.class);
				if (!resetCompleted) {
					System.out.println("모든 유저의 Guess state 를 초기화 하는 중 오류가 발생하였습니다");
				}
			}

			if (!hasErrorMessage) {
				count++;
			}
		}
	}

	private Setting fetchGameSetting(long gameRoomId) throws IOException {
		client.sendSocketData("GET_SETTING," + gameRoomId);
		final String settingJson = client.getServerMessage();
		return objectMapper.readValue(settingJson, Setting.class);
	}

	private List<OldGameRoom> fetchGameRoomList() throws IOException {
		client.sendSocketData("GET_ROOM_LIST");
		return objectMapper.readValue(client.getServerMessage(), objectMapper.getTypeFactory().constructCollectionType
				(List.class, OldGameRoom.class));
	}

	private void showingGameRoomMenu(long gameRoomId) throws IOException {
		boolean menuLeft = false;
		final Setting oSetting = fetchGameSetting(gameRoomId);
		final Setting newSetting = new Setting(oSetting.getLimitWrongInputCount(), oSetting.getLimitGuessInputCount(),
				oSetting.getGenerationNumberCount());
		while (!menuLeft) {
			String limitWrongInputCountMessage = String.valueOf(oSetting.getLimitWrongInputCount());
			if (oSetting.getLimitWrongInputCount() != newSetting.getLimitWrongInputCount()) {
				limitWrongInputCountMessage = oSetting.getLimitWrongInputCount() + " -> " + newSetting
						.getLimitWrongInputCount();
			}

			String limitGuessInputCountMessage = String.valueOf(oSetting.getLimitGuessInputCount());
			if (oSetting.getLimitGuessInputCount() != newSetting.getLimitGuessInputCount()) {
				limitGuessInputCountMessage = oSetting.getLimitGuessInputCount() + " -> " + newSetting
						.getLimitGuessInputCount();
			}

			String generationNumberCountMessage = String.valueOf(oSetting.getGenerationNumberCount());
			if (oSetting.getGenerationNumberCount() != newSetting.getGenerationNumberCount()) {
				generationNumberCountMessage = oSetting.getGenerationNumberCount() + " -> " + newSetting
						.getGenerationNumberCount();
			}
			System.out.println("======= 메뉴를 선택해주세요 =======");
			System.out.println("1. 입력 오류 횟수 설정 (" + limitWrongInputCountMessage + ")");
			System.out.println("2. 공격 횟수 설정 (" + limitGuessInputCountMessage + ")");
			System.out.println("3. 생성 숫자 자리수 설정 (" + generationNumberCountMessage + ")");
			System.out.println("4. 저장(저장을 하지 않으면 이전 설정유지)");
			System.out.println("0. 메뉴 나가기");
			Scanner settingInput = new Scanner(System.in);
			if (settingInput.hasNextInt()) {
				switch (settingInput.nextInt()) {
					case 1:
						System.out.print("값을 입력해주세요. : ");
						Scanner limitWrongNumSettingScanner = new Scanner(System.in);
						if (limitWrongNumSettingScanner.hasNextInt()) {
							newSetting.setLimitWrongInputCount(Integer.parseInt(limitWrongNumSettingScanner.nextLine
									()));
						}
						break;
					case 2:
						System.out.print("값을 입력해주세요. : ");
						Scanner limitGuessNumSettingScanner = new Scanner(System.in);
						if (limitGuessNumSettingScanner.hasNextInt()) {
							newSetting.setLimitGuessInputCount(Integer.parseInt(limitGuessNumSettingScanner.nextLine
									()));
						}
						break;
					case 3:
						System.out.print("값을 입력해주세요. : ");
						Scanner generationNumCountSettingScanner = new Scanner(System.in);
						if (generationNumCountSettingScanner.hasNextInt()) {
							newSetting.setGenerationNumberCount(Integer.parseInt(generationNumCountSettingScanner
									.nextLine()));
						}
						break;
					case 4:
						client.sendSocketData("SET_SETTING," + gameRoomId + ":wrong:" + newSetting
								.getLimitWrongInputCount() + ":guess:" + newSetting.getLimitGuessInputCount() +
								":count:" + newSetting.getGenerationNumberCount());
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

	public void startGame() throws IOException, InterruptedException {
		Boolean gameTerminated = false;

		System.out.println("====== 야구게임을 시작합니다 ======");
		System.out.println();

		login();

		System.out.println();
		while (!gameTerminated) {
			System.out.println("*** 게임룸 리스트 ***");
			final List<OldGameRoom> gameRoomList = fetchGameRoomList();
			if (gameRoomList.isEmpty()) {
				System.out.println("생성된 게임룸이 없습니다.");
			} else {
				for (OldGameRoom gameRoom : gameRoomList) {
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
						System.out.print("게임룸 이름을 입력해주세요 : ");
						Scanner gameRoomNameScanner = new Scanner(System.in);
						if (gameRoomNameScanner.hasNextLine()) {
							final String gameRoomName = gameRoomNameScanner.nextLine();
							final OldGameRoom createdGameRoom = createGameRoom(gameRoomName);

							user.setRole(selectUserRole(createdGameRoom.getId()));

							if (joiningGameRoom(createdGameRoom.getId())) {
								joinGameRoom(createdGameRoom.getId());
							}
						}
						break;
					case 2:
						System.out.print("게임룸 번호 선택 : ");
						Scanner gameRoomIdScanner = new Scanner(System.in);
						if (gameRoomIdScanner.hasNextLine()) {
							final long gameRoomId = Long.valueOf(gameRoomIdScanner.nextLine());

							user.setRole(selectUserRole(gameRoomId));

							if (joiningGameRoom(gameRoomId)) {
								joinGameRoom(gameRoomId);
							}
						}
						break;
					case 0:
						System.out.println("게임을 끝냅니다.");
						gameTerminated = true;
						client.closeConnection();
						break;
				}
			}
		}
	}

	private void login() throws IOException {
		boolean loginCompleted = false;
		while (!loginCompleted) {
			System.out.print("유저 아이디를 입력해주세요 : ");
			Scanner userNameScanner = new Scanner(System.in);
			if (userNameScanner.hasNextLine()) {
				final String userId = userNameScanner.nextLine();

				client.sendSocketData("LOGIN," + userId);
				final String errorMessageJson = client.getServerMessage();
				final ErrorMessage errorMessage = objectMapper.readValue(errorMessageJson, ErrorMessage.class);
				if (errorMessage.getMessage() != null && !errorMessage.getMessage().isEmpty()) {
					System.out.println(errorMessage.getMessage());
				} else {
					this.user = new OldUser(userId, null, new Score());
					loginCompleted = true;
				}
			}
		}
	}

	private Role selectUserRole(long gameRoomId) throws IOException {
		String userRole = null;
		boolean userRoleSelectCompleted = false;
		while (!userRoleSelectCompleted) {
			System.out.print("1. 공격(숫자 맞추기), 2. 수비(숫자생성) 중에 하나의 역할을 선택해주세요 : ");
			Scanner userRoleScanner = new Scanner(System.in);
			userRole = "ATTACKER";
			if (userRoleScanner.hasNextLine()) {
				final String userRoleSelect = userRoleScanner.nextLine();
				if (userRoleSelect.equals("2")) {
					userRole = "DEPENDER";
					client.sendSocketData("DEPENDER_ALREADY_EXIST," + gameRoomId);
					final String alreadyExistJson = client.getServerMessage();
					final boolean alreadyExist = objectMapper.readValue(alreadyExistJson, Boolean.class);

					if (alreadyExist) {
						System.out.println("수비는 이미 존재 하기 때문에 선택 할 수 없습니다. 공격을 선택해주세요.");
					} else {
						userRoleSelectCompleted = true;
					}
				} else {
					userRoleSelectCompleted = true;
				}
			}
		}

		return new Role(GameRole.valueOf(userRole));
	}

	private boolean joiningGameRoom(long gameRoomId) throws IOException {
		client.sendSocketData("JOIN," + gameRoomId + ":USER:" + user.getEmail() + ":ROLE:" +
				user.getRole().getRoleType().name());
		final String errorMessageJson = client.getServerMessage();
		final ErrorMessage errorMessage = objectMapper.readValue(errorMessageJson, ErrorMessage.class);

		boolean joinCompleted;
		if (errorMessage != null && errorMessage.getMessage() != null && !errorMessage.getMessage().isEmpty()) {
			joinCompleted = false;
			System.out.println(errorMessage.getMessage());
		} else {
			joinCompleted = true;
		}

		return joinCompleted;
	}

	private void joinGameRoom(long gameRoomId) throws IOException, InterruptedException {
		System.out.println("안녕하세요 " + user.getEmail() + "님, " + gameRoomId + "번 방에 입장하셨습니다");

		boolean gameRoomLeft = false;
		while (!gameRoomLeft) {
			final List<OldGameRoom> gameRoomList = fetchGameRoomList();

			final OldGameRoom joinedGameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
					(Collectors.toList()).get(0);
			System.out.println("----- 게임룸 (" + joinedGameRoom.getName() + ") -----");
			final String userList = joinedGameRoom.getUsers().stream().map(OldUser::getEmail).collect(Collectors.joining
					("," +
					" " +
					"" + ""));
			System.out.println("방장 : " + joinedGameRoom.getOwner().getEmail());
			System.out.println("접속 유저 : " + userList);
			final Setting setting = fetchGameSetting(gameRoomId);
			System.out.println("** 현재 설정 **");
			System.out.println("* 공격 횟수 " + setting.getLimitGuessInputCount() + "회");
			System.out.println("* 생성 갯수 " + setting.getGenerationNumberCount() + "개");
			System.out.println("* 입력오류제한 : " + setting.getLimitWrongInputCount() + "회");
			System.out.println();
			System.out.println("----- 메뉴 -----");
			System.out.println("1. 준비");
			if (joinedGameRoom.getOwner().getEmail().equals(user.getEmail())) {
				System.out.println("2. 설정");
			}
			System.out.println("0. 게임룸 나가기");
			System.out.println("---------------");
			System.out.print("메뉴를 선택해 주세요 : ");
			Scanner roomMenuScanner = new Scanner(System.in);
			if (roomMenuScanner.hasNextLine()) {
				final int selectedMenu = Integer.valueOf(roomMenuScanner.nextLine());
				switch (selectedMenu) {
					case 1:
						if (user.getRole().getRoleType().equals(GameRole.DEFENDER)) {
							generateNumber(gameRoomId);
						}

						client.sendSocketData("READY," + gameRoomId + ":USER_ID:" + user.getEmail());

						boolean allUsersReady = false;
						while (!allUsersReady) {
							// TODO 옵저버 패턴이나 콜백 패턴 사용해보기
							client.sendSocketData("GET_READY_STATE," + gameRoomId);
							final String readyStateJson = client.getServerMessage();
							final Boolean readyState = objectMapper.readValue(readyStateJson, Boolean.class);
							if (readyState) {
								allUsersReady = true;
							}

							// 0.5초에 한번씩 확인
							Thread.sleep(500);
						}

						if (user.getRole().getRoleType().equals(GameRole.DEFENDER)) {
							monitoringGame(gameRoomId);
						} else {
							runningGame(gameRoomId);
						}
						break;
					case 2:
						showingGameRoomMenu(gameRoomId);
						break;
					case 0:
						System.out.println("안녕히가세요");
						gameRoomLeft = true;
						break;
					default:
						break;
				}
			}
		}
	}

	private void monitoringGame(long gameRoomId) throws IOException {
		boolean allUsersResolved = false;
		while (!allUsersResolved) {
			final String resultDtoJson = client.getServerMessage();
			final ResultDto resultDto = objectMapper.readValue(resultDtoJson, ResultDto.class);

			allUsersResolved = resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRole().getRoleType()
					.equals(GameRole.ATTACKER) && u.getGameOver()).count() == resultDto.getGameRoom().getUsers()
					.stream().filter(u -> u.getRole().getRoleType().equals(GameRole.ATTACKER)).count();

			if (allUsersResolved) {
				System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
				System.out.println("모든 유저가 게임을 마쳤습니다");
				System.out.println("게임 결과");
				long allUserCount = resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRole().getRoleType()
						.equals(GameRole.ATTACKER)).count();
				for (int i = 1; i <= allUserCount; i++) {
					final int rankValue = i;
					final OldUser attacker = resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRank()
							.getValue() == rankValue).findFirst().get();
					System.out.println(attacker.getRank().getValue() + "등 : " + attacker.getEmail() + ", 점수 : " +
							attacker.getCurrentScore().getValue());
				}

				System.out.println("****************************************************");
				client.sendSocketData("GET_DEPENDER_SCORE," + gameRoomId + ":USER_ID:" + user.getEmail());
				final String dependerScoreJson = client.getServerMessage();
				final Score dependerScore = objectMapper.readValue(dependerScoreJson, Score.class);
				System.out.println("수비 " + user.getEmail() + "님의 점수는 : " + dependerScore.getValue() + "입니다.");
				System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			} else {
				if (resultDto.getResult().getSettlement().isSolved()) {
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					System.out.println("생성숫자 : " + resultDto.getGameRoom().getGenerationNumbers());
					System.out.println(resultDto.getUser().getEmail() + " 유저의 입력 : " + resultDto.getUser().getGuessNum());
					System.out.println("추측 결과 : " + resultDto.getResult().getStrike().getValue() + "스트라이크, " +
							resultDto.getResult().getBall().getValue() + "볼");

					System.out.println("등수 : " + resultDto.getGameRoom().getUsers().stream().filter(u -> u.getRole()
							.getRoleType().equals(GameRole.ATTACKER)).count() + "명중 " + resultDto.getUser().getRank()
							.getValue() + "등");
					System.out.println("점수 : " + resultDto.getScore().getValue() + "점");
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
				} else {
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					System.out.println("생성숫자 : " + resultDto.getGameRoom().getGenerationNumbers());
					System.out.println(resultDto.getUser().getEmail() + " 유저의 입력 : " + resultDto.getUser().getGuessNum());
					System.out.println("추측 결과 : " + resultDto.getResult().getStrike().getValue() + "스트라이크, " +
							resultDto.getResult().getBall().getValue() + "볼");
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
				}
			}
		}
	}

	// TODO 유저가 임의로 입력한 숫자의 자리수 및 중복수 체크 로직 넣을것
	private void generateNumber(long gameRoomId) throws IOException {
		boolean generateNumberCompleted = false;
		while (!generateNumberCompleted) {
			final int generationNumberCount = fetchGameSetting(gameRoomId).getGenerationNumberCount();
			System.out.print("0 ~ 9 사이의 숫자중 중복되지 않게 " + generationNumberCount + "자리의 숫자를 입력해주세요 : ");
			Scanner generateNumScanner = new Scanner(System.in);
			if (generateNumScanner.hasNextLine()) {
				final String generatedNum = generateNumScanner.nextLine();
				if (setGenerationNumberToGameRoom(gameRoomId, generatedNum)) {
					generateNumberCompleted = true;
				}
			}
		}
	}

	private boolean setGenerationNumberToGameRoom(long gameRoomId, String generatedNumber) throws IOException {
		boolean setCompleted = false;
		client.sendSocketData("SET_GENERATION_NUMBER," + generatedNumber + ":ROOM_ID:" + gameRoomId);
		final String errorMessageJson = client.getServerMessage();
		final ErrorMessage errorMessage = objectMapper.readValue(errorMessageJson, ErrorMessage.class);

		if (errorMessage.getMessage() != null && !errorMessage.getMessage().isEmpty()) {
			System.out.println(errorMessage.getMessage());
		} else {
			setCompleted = true;
		}

		return setCompleted;
	}

	private OldGameRoom createGameRoom(String gameRoomName) throws IOException {
		client.sendSocketData("CREATE_ROOM," + gameRoomName + ":USER_ID:" + user.getEmail());
		final String createdRoomJson = client.getServerMessage();
		return objectMapper.readValue(createdRoomJson, OldGameRoom.class);
	}
}