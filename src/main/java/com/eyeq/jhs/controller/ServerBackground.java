package com.eyeq.jhs.controller;

import com.eyeq.jhs.factory.GameRoomMaker;
import com.eyeq.jhs.model.ErrorMessage;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.model.ResultDto;
import com.eyeq.jhs.model.Role;
import com.eyeq.jhs.model.Score;
import com.eyeq.jhs.model.ScoreCalculator;
import com.eyeq.jhs.model.User;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;
import com.eyeq.jhs.strategy.RandomNumberGenerator;
import com.eyeq.jhs.type.ErrorType;
import com.eyeq.jhs.type.MessageType;
import com.eyeq.jhs.type.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;

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

		public void sendErrorMessageToAllRoomMembers(User excludeUser, String message) throws IOException {
			final ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setMessage(message);
			sendMessageAllClients(clients.get(excludeUser), objectMapper.writeValueAsString(errorMessage));
		}

		public void sendBgErrorMessageToAllRoomMembers(GameRoom gameRoom, String message) {
			final ErrorMessage errorMessage = new ErrorMessage();
			gameRoom.getUsers().forEach(u -> {
				try {
					errorMessage.setMessage(message);

					sendMessageAllClients(bgMessageClients.get(u), objectMapper.writeValueAsString(errorMessage));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		public void sendMessageAllClients(DataOutputStream dataOutputStream, String message) throws IOException {
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

								final long readyCount = gameRoom.getUsers().stream().filter(User::isReady).count();
								final int totalUserCount = gameRoom.getUsers().size();

								String message;
								if (readyCount == totalUserCount) {
									message = null;
								} else {
									message = "게임이 준비중 입니다. 게임 준비를 선택해 주세요.\n" +
											"모든 유저가 준비되면 게임이 시직됩니다.\n" +
											"대기중.. (" + readyCount + "/" + totalUserCount + ")";
								}

								sendErrorMessageToAllRoomMembers(user, message);
								sendBgErrorMessageToAllRoomMembers(gameRoom, message);
							}
							break;
						case START:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.parseLong(clientSendValues[0]);
								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final String userId = clientSendValues[2];
								final User user = gameRoom.getUsers().stream().filter(u -> u.getId().equals(userId))
										.findFirst().get();
								user.setReady(true);

								final String generationNumber = clientSendValues[4];
								gameRoom.setGenerationNumbers(generationNumber);

								final long readyCount = gameRoom.getUsers().stream().filter(User::isReady).count();
								final int totalUserCount = gameRoom.getUsers().size();
								String message;
								if (readyCount == totalUserCount) {
									message = null;
								} else {
									message = "게임이 준비중 입니다.\n" +
											"모든 유저가 준비되면 게임이 시직됩니다.\n" +
											"대기중.. (" + readyCount + "/" + totalUserCount + ")";
								}

								sendErrorMessageToAllRoomMembers(user, message);
								sendBgErrorMessageToAllRoomMembers(gameRoom, message);
							}
							break;
						case GUESS_NUM:
							try {
								if (value != null) {
									final String[] clientSendValues = value.split(":");
									final long gameRoomId = Long.parseLong(clientSendValues[0]);
									GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId)
											.collect(Collectors.toList()).get(0);

									final String userId = clientSendValues[2];
									final User user = gameRoom.getUsers().stream().filter(u -> u.getId().equals
											(userId)).findFirst().get();

									gameEngine.userInputValidate(clientSendValues[4], gameRoom.getSetting());
									user.setGuessCount(user.getGuessCount() + 1);
									Result result = gameEngine.checkNumber(value);
									if (result.getSolve().isValue()) {
										user.setReady(false);
										user.setGuessCount(0);
									} else if (!result.getSolve().isValue() && user.getGuessCount() == gameRoom
											.getSetting().getLimitGuessInputCount()) {
										user.setReady(false);
										user.setGuessCount(0);
										result = null;
									}
									Score score = ScoreCalculator.calculateScore(user.getGuessCount(), result);

									ResultDto resultDto = new ResultDto(result, user, gameRoom, score, null);

									jsonResult = objectMapper.writeValueAsString(resultDto);

									dataOutputStream.writeUTF(jsonResult);
								}
							} catch (IllegalArgumentException e) {

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
						default:
							break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}