package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.factory.GameRoomMaker;
import kr.co.leehana.model.ErrorMessage;
import kr.co.leehana.model.OldGameRoom;
import kr.co.leehana.model.OldUser;
import kr.co.leehana.model.Rank;
import kr.co.leehana.model.Result;
import kr.co.leehana.model.ResultDto;
import kr.co.leehana.model.Role;
import kr.co.leehana.model.Score;
import kr.co.leehana.enums.ErrorType;
import kr.co.leehana.enums.GameRole;
import kr.co.leehana.enums.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerBackground {

	private ServerSocket server = null;
	private ServerSocket bgMessageServer = null;
	private ObjectMapper objectMapper = new ObjectMapper();

	private List<OldGameRoom> gameRoomList = new ArrayList<>();
	private Map<OldUser, DataOutputStream> clients = new HashMap<>();
	private Map<OldUser, DataOutputStream> bgMessageClients = new HashMap<>();

	public ServerBackground() {
		Collections.synchronizedList(gameRoomList);
		Collections.synchronizedMap(clients);
		Collections.synchronizedMap(bgMessageClients);
	}

	public void startServer() {
		try {
			server = new ServerSocket(9090);
			bgMessageServer = new ServerSocket(9191);
			while (true) {
				System.out.println("Server: Waiting for request.");
				final Socket clientSocket = server.accept();
				System.out.println("Server: accepted.");

				System.out.println("Background Message Server: Waiting for request.");
				final Socket bgMessageClientSocket = bgMessageServer.accept();
				System.out.println("Background Message Server: accepted.");

				final GenerationNumberStrategy strategy = new RandomNumberGenerator();
				final ServerController receiver = new ServerController(new GameController(strategy), clientSocket,
						bgMessageClientSocket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					// 소켓수신빌요없는 경우 서버소켓종료
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class ServerController extends Thread {

		private GameController gameEngine;
		private DataInputStream dataInputStream;
		private DataOutputStream dataOutputStream;

		private DataOutputStream bgMessageClientOutputStream;

		public ServerController(GameController gameController, Socket socket, Socket bgMessageClientSocket) {
			this.gameEngine = gameController;
			try {
				this.dataInputStream = new DataInputStream(socket.getInputStream());
				this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
				this.bgMessageClientOutputStream = new DataOutputStream(bgMessageClientSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendBgMessageToAllRoomMembers(OldGameRoom gameRoom, String message) {
			gameRoom.getUsers().forEach(u -> {
				try {
					sendMessage(bgMessageClients.get(u), message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		public void sendMessage(DataOutputStream dataOutputStream, String message) throws IOException {
			dataOutputStream.writeUTF(message);
		}

		@Override
		public void run() {
			try {
				while (dataInputStream != null) {
					final String clientMsg = dataInputStream.readUTF();
					System.out.println("Msg : " + clientMsg);

					String messageTypeStringValue;
					String value = null;
					MessageType messageType;
					if (clientMsg.contains(",")) {
						messageTypeStringValue = clientMsg.split(",")[0];
						value = clientMsg.split(",")[1];
					} else {
						messageTypeStringValue = clientMsg;
					}
					messageType = MessageType.valueOf(messageTypeStringValue);

					String jsonResult;
					switch (messageType) {
						case CREATE_ROOM:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final String gameRoomName = clientSendValues[0];
								final String userId = clientSendValues[2];
								final OldUser ownerUser = clients.entrySet().stream().filter(entry -> entry.getKey()
										.getEmail().equals(userId)).map(Map.Entry::getKey).collect(Collectors.toList())
										.get(0);
								final OldGameRoom newGameRoom = GameRoomMaker.make(gameRoomList, gameRoomName, ownerUser);
								gameRoomList.add(newGameRoom);
								jsonResult = objectMapper.writeValueAsString(newGameRoom);
								dataOutputStream.writeUTF(jsonResult);
							}
							break;
						case JOIN:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.valueOf(clientSendValues[0]);
								final String userId = clientSendValues[2];
								final String role = clientSendValues[4];
								final OldUser joinedUser = clients.entrySet().stream().filter(entry -> entry.getKey()
										.getEmail().equals(userId)).map(Map.Entry::getKey).collect(Collectors.toList())
										.get(0);
								joinedUser.setRole(new Role(GameRole.valueOf(role)));
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								ErrorMessage errorMessage = new ErrorMessage();
								if (gameRoom.getUsers().stream().filter(u -> u.getEmail().equals(joinedUser.getEmail()))
										.count() > 0) {
									errorMessage.setMessage(ErrorType.ALREADY_JOIN.getMessage());
								} else {
									gameRoom.getUsers().add(joinedUser);
								}

								dataOutputStream.writeUTF(objectMapper.writeValueAsString(errorMessage));
							}
							break;
						case GET_ROOM_LIST:
							jsonResult = objectMapper.writeValueAsString(gameRoomList);
							dataOutputStream.writeUTF(jsonResult);
							break;
						case READY:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.parseLong(clientSendValues[0]);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final String userId = clientSendValues[2];
								final OldUser user = gameRoom.getUsers().stream().filter(u -> u.getEmail().equals(userId))
										.findFirst().get();

								resetUser(user);

								user.setReady(true);

								final long readyCount = gameRoom.getUsers().stream().filter(OldUser::getReady).count();
								final int totalUserCount = gameRoom.getUsers().size();

								if (readyCount < totalUserCount) {
									final String message = "게임이 준비중 입니다. 모든 유저가 준비되면 게임이 시직됩니다.\n" +
											"대기중.. (" + readyCount + "/" + totalUserCount + ")";
									sendBgMessageToAllRoomMembers(gameRoom, message);
								} else {
									final String message = "모든 유저의 준비가 완료 되었습니다.";
									sendBgMessageToAllRoomMembers(gameRoom, message);
								}
							}
							break;
						case START:
							if (value != null) {
								final long gameRoomId = Long.parseLong(value);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								if (gameRoom.getGenerationNumbers() == null || gameRoom.getGenerationNumbers().isEmpty
										()) {
									gameRoom.setGenerationNumbers(gameEngine.generateNumber(gameRoom.getSetting()));
								}
							}
							break;
						case GUESS_NUM:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.parseLong(clientSendValues[2]);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final String userId = clientSendValues[4];
								final OldUser user = gameRoom.getUsers().stream().filter(u -> u.getEmail().equals(userId))
										.findFirst().get();
								user.setGuessNum(clientSendValues[0]);

								final ErrorMessage errorMessage = new ErrorMessage();
								try {
									gameEngine.userInputValidation(user.getGuessNum(), gameRoom.getSetting());
								} catch (IllegalArgumentException e) {
									user.setWrongCount(user.getWrongCount() + 1);
									errorMessage.setMessage("숫자 입력이 잘못 되었습니다. 다시 시도해주세요. " + user.getWrongCount() +
											"/" + gameRoom.getSetting().getLimitWrongInputCount());
								}

								Result result = null;
								if (errorMessage.getMessage() == null || errorMessage.getMessage().isEmpty()) {
									user.setWrongCount(0);
									user.setGuessCount(user.getGuessCount() + 1);
									result = gameEngine.compareNumber(gameRoom.getGenerationNumbers(), user
											.getGuessNum());
									if (result.getSettlement().isSolved()) {
										user.setGameOver(true);

										int ranking = (int) (gameRoom.getUsers().stream().filter(u -> u.getRank() !=
												null && u.getRank().getValue() > 0).count() + 1);

										user.setRank(new Rank(ranking));
									} else if (!result.getSettlement().isSolved() && user.getGuessCount() == gameRoom
											.getSetting().getLimitGuessInputCount()) {
										user.setGameOver(true);
										result = null;
									}

									user.setResult(result);
									user.setGuessCompleted(true);
								}

								if (user.getWrongCount() >= gameRoom.getSetting().getLimitWrongInputCount()) {
									user.setGameOver(true);
								}

								Score score = null;
								if (user.getGameOver()) {
									score = ScoreCalculator.calculation(user, gameRoom);

									if (user.getTotalScore() == null) {
										user.setTotalScore(score);
									} else {
										user.getTotalScore().setValue(user.getTotalScore().getValue() + score.getValue
												());
									}
								}

								user.setCurrentScore(score);

								if (gameRoom.getUsers().stream().filter(OldUser::getGameOver).count() == gameRoom
										.getUsers().size()) {
									gameRoom.setGenerationNumbers(null);
									gameRoom.getUsers().forEach(u -> u.setReady(false));
								}

								ResultDto resultDto = new ResultDto(result, user, gameRoom, score, errorMessage);

								jsonResult = objectMapper.writeValueAsString(resultDto);
								dataOutputStream.writeUTF(jsonResult);

								final OldUser depender = gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType
										().equals(GameRole.DEFENDER)).findFirst().orElse(null);
								if (depender != null) {
									clients.get(depender).writeUTF(jsonResult);
								}
							}
							break;
						case GET_SETTING:
							if (value != null) {
								final long gameRoomId = Long.parseLong(value);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);
								jsonResult = objectMapper.writeValueAsString(gameRoom.getSetting());
								dataOutputStream.writeUTF(jsonResult);
							}
							break;
						case SET_SETTING:
							if (value != null) {
								// TODO json object 로 처리할것
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.parseLong(clientSendValues[0]);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								//잘못된 숫자 입력 횟수 제한
								final int limitWrongInputCount = Integer.parseInt(clientSendValues[2]);

								// 야구 게임 횟수
								final int limitGuessInputCount = Integer.parseInt(clientSendValues[4]);

								// 생성 숫자 갯수
								final int generationNumberCount = Integer.parseInt(clientSendValues[6]);

								gameRoom.getSetting().setLimitWrongInputCount(limitWrongInputCount);
								gameRoom.getSetting().setLimitGuessInputCount(limitGuessInputCount);
								gameRoom.getSetting().setGenerationNumberCount(generationNumberCount);

								jsonResult = objectMapper.writeValueAsString(gameRoom.getSetting());
								dataOutputStream.writeUTF(jsonResult);
							}
							break;
						case LOGIN:
							if (value != null) {
								OldUser newUser = new OldUser(value, null, new Score());
								ErrorMessage errorMessage = new ErrorMessage();

								if (clients.entrySet().stream().filter(entry -> entry.getKey().getEmail().equals(newUser
										.getEmail())).count() > 0) {
									errorMessage.setMessage(ErrorType.DUPLICATE_USER_ID.getMessage());
								} else {
									clients.put(newUser, dataOutputStream);
									bgMessageClients.put(newUser, bgMessageClientOutputStream);
								}

								jsonResult = objectMapper.writeValueAsString(errorMessage);
								dataOutputStream.writeUTF(jsonResult);
							}
							break;
						case LOGOUT:
							if (value != null) {
								final String userId = value;
								OldUser leaveUser = clients.entrySet().stream().filter(entry -> entry.getKey().getEmail()
										.equals(userId)).map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
								clients.remove(leaveUser);
								bgMessageClients.remove(leaveUser);
							}
							break;
						case GET_READY_STATE:
							if (value != null) {
								broadCastCurrentState(value, messageType);
							}
							break;
						case SET_GENERATION_NUMBER:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final String generationNumbers = clientSendValues[0];
								final ErrorMessage errorMessage = new ErrorMessage();
								gameEngine.generatedNumbersValidator(generationNumbers, errorMessage);

								if (errorMessage.getMessage() == null || errorMessage.getMessage().isEmpty()) {
									final long gameRoomId = Long.parseLong(clientSendValues[2]);
									OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId)
											.collect(Collectors.toList()).get(0);
									gameRoom.setGenerationNumbers(generationNumbers);
								}

								dataOutputStream.writeUTF(objectMapper.writeValueAsString(errorMessage));
							}
							break;
						case ALL_USER_COMPLETED_GUESS:
							if (value != null) {
								broadCastCurrentState(value, messageType);
							}
							break;
						case ALL_USER_GUESS_COMPLETE_STATE_RESET:
							if (value != null) {
								final long gameRoomId = Long.parseLong(value);
								OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);
								gameRoom.getUsers().forEach(u -> u.setGuessCompleted(false));
								dataOutputStream.writeUTF(objectMapper.writeValueAsString(true));
							}
							break;
						case DEPENDER_ALREADY_EXIST:
							if (value != null) {
								final long gameRoomId = Long.parseLong(value);
								final OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);
								boolean dependerAlreadyExist = gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole
										.DEFENDER)).count() > 0;
								dataOutputStream.writeUTF(objectMapper.writeValueAsString(dependerAlreadyExist));
							}
							break;
						case GET_DEPENDER_SCORE:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.parseLong(clientSendValues[0]);
								final String userId = clientSendValues[2];
								final OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);
								final OldUser depender = gameRoom.getUsers().stream().filter(u -> u.getEmail().equals(userId)).findFirst().get();
								final Score score = ScoreCalculator.calculation(depender, gameRoom);
								dataOutputStream.writeUTF(objectMapper.writeValueAsString(score));
							}
							break;
						default:
							break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void broadCastCurrentState(String value, MessageType messageType) throws IOException {
			final long gameRoomId = Long.parseLong(value);
			OldGameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect(Collectors.toList()
			).get(0);
			boolean currentState = false;
			switch (messageType) {
				case GET_READY_STATE:
					currentState = gameRoom.getUsers().stream().filter(OldUser::getReady).count() == gameRoom.getUsers()
							.size();
					break;
				case ALL_USER_COMPLETED_GUESS:
					currentState = gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole
							.ATTACKER) && u.isGuessCompleted()).count() == gameRoom.getUsers().stream().filter(u -> u
							.getRole().getRoleType().equals(GameRole.ATTACKER)).count();
					break;
			}

			final String messageJson = objectMapper.writeValueAsString(currentState);
			dataOutputStream.writeUTF(messageJson);
		}
	}

	private void resetUser(OldUser user) {
		user.setGameOver(false);
		user.setGuessCompleted(false);
		user.setGuessCount(0);
		user.setRank(null);
		user.setReady(false);
		user.setResult(null);
		user.setWrongCount(0);
	}
}