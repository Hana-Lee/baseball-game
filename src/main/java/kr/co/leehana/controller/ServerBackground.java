package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.factory.GameRoomMaker;
import kr.co.leehana.model.ErrorMessage;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Rank;
import kr.co.leehana.model.Result;
import kr.co.leehana.model.ResultDto;
import kr.co.leehana.model.Role;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.ScoreCalculator;
import kr.co.leehana.model.User;
import kr.co.leehana.type.ErrorType;
import kr.co.leehana.type.MessageType;
import kr.co.leehana.type.RoleType;

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

	private List<GameRoom> gameRoomList = new ArrayList<>();
	private Map<User, DataOutputStream> clients = new HashMap<>();
	private Map<User, DataOutputStream> bgMessageClients = new HashMap<>();

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

		public void sendBgMessageToAllRoomMembers(GameRoom gameRoom, String message) {
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
								final User ownerUser = clients.entrySet().stream().filter(entry -> entry.getKey()
										.getId().equals(userId)).map(Map.Entry::getKey).collect(Collectors.toList())
										.get(0);
								final GameRoom newGameRoom = GameRoomMaker.make(gameRoomList, gameRoomName, ownerUser);
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
								final User joinedUser = clients.entrySet().stream().filter(entry -> entry.getKey()
										.getId().equals(userId)).map(Map.Entry::getKey).collect(Collectors.toList())
										.get(0);
								joinedUser.setRole(new Role(RoleType.valueOf(role)));
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								ErrorMessage errorMessage = new ErrorMessage();
								if (gameRoom.getUsers().stream().filter(u -> u.getId().equals(joinedUser.getId()))
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
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final String userId = clientSendValues[2];
								final User user = gameRoom.getUsers().stream().filter(u -> u.getId().equals(userId))
										.findFirst().get();
								user.setReady(true);

								final long readyCount = gameRoom.getUsers().stream().filter(User::getReady).count();
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
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
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
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final String userId = clientSendValues[4];
								final User user = gameRoom.getUsers().stream().filter(u -> u.getId().equals(userId))
										.findFirst().get();

								final ErrorMessage errorMessage = new ErrorMessage();
								try {
									gameEngine.userInputValidation(clientSendValues[0], gameRoom.getSetting());
								} catch (IllegalArgumentException e) {
									errorMessage.setMessage("숫자 입력이 잘못 되었습니다. 다시 시도해주세요.");
									user.setWrongCount(user.getWrongCount() + 1);
								}

								Result result = null;
								Score score = null;
								if (errorMessage.getMessage() == null || errorMessage.getMessage().isEmpty()) {
									user.setWrongCount(0);
									user.setGuessCount(user.getGuessCount() + 1);
									result = gameEngine.compareNumber(gameRoom.getGenerationNumbers(), value);
									if (result.getSettlement().isSolved()) {
										user.setReady(false);
										user.setGuessCount(0);
										user.setGameOver(true);
										user.setWrongCount(0);
										gameRoom.setGenerationNumbers(null);

										int ranking = (int) (gameRoom.getUsers().stream().filter(u -> u.getRank() !=
												null && u.getRank().getRanking() > 0).count() + 1);

										user.setRank(new Rank(ranking));
									} else if (!result.getSettlement().isSolved() && user.getGuessCount() == gameRoom
											.getSetting().getLimitGuessInputCount()) {
										user.setReady(false);
										user.setGuessCount(0);
										user.setGameOver(true);
										user.setWrongCount(0);
										result = null;
									}

									if (gameRoom.getUsers().stream().filter(User::getGameOver).count() == gameRoom
											.getUsers().size()) {
										gameRoom.setGenerationNumbers(null);
									}

									user.setResult(result);

									score = ScoreCalculator.calculation(user, gameRoom);
									user.setGuessCompleted(true);
								}

								ResultDto resultDto = new ResultDto(result, user, gameRoom, score, errorMessage);

								jsonResult = objectMapper.writeValueAsString(resultDto);
								dataOutputStream.writeUTF(jsonResult);
							}
							break;
						case GET_SETTING:
							if (value != null) {
								final long gameRoomId = Long.parseLong(value);
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
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
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
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
								User newUser = new User(value, null, false);
								ErrorMessage errorMessage = new ErrorMessage();

								if (clients.entrySet().stream().filter(entry -> entry.getKey().getId().equals(newUser
										.getId())).count() > 0) {
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
								User leaveUser = clients.entrySet().stream().filter(entry -> entry.getKey().getId()
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
									GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId)
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
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);
								gameRoom.getUsers().forEach(u -> u.setGuessCompleted(false));
								dataOutputStream.writeUTF(objectMapper.writeValueAsString(true));
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
			GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect(Collectors.toList()
			).get(0);
			boolean currentState = false;
			switch (messageType) {
				case GET_READY_STATE:
					currentState = gameRoom.getUsers().stream().filter(User::getReady).count() == gameRoom.getUsers()
							.size();
					break;
				case ALL_USER_COMPLETED_GUESS:
					currentState = gameRoom.getUsers().stream().filter(User::isGuessCompleted).count() == gameRoom
							.getUsers().size();
					break;
			}

			final String messageJson = objectMapper.writeValueAsString(currentState);
			dataOutputStream.writeUTF(messageJson);
		}
	}
}