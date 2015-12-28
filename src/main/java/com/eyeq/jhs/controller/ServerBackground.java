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
import java.util.List;
import java.util.stream.Collectors;

public class ServerBackground {

	private ServerSocket server = null;
	private ServerSocket backgroundMessageServer = null;
	private Socket client = null;
	private Socket backgroundMessageClient = null;

	private List<GameRoom> gameRoomList = new ArrayList<>();
	private List<DataOutputStream> backgroundMessageClients = new ArrayList<>();

	public ServerBackground() {
		Collections.synchronizedList(gameRoomList);
	}

	public void startServer() {
		try {
			server = new ServerSocket(9090);
			backgroundMessageServer = new ServerSocket(9191);
			while (true) {
				System.out.println("Server: Waiting for request.");
				client = server.accept();
				System.out.println("Server: accepted.");

				System.out.println("Background Message Server: Waiting for request.");
				backgroundMessageClient = backgroundMessageServer.accept();
				System.out.println("Background Message Server: accepted.");
				backgroundMessageClients.add(new DataOutputStream(backgroundMessageClient.getOutputStream()));

				final GenerationNumberStrategy strategy = new RandomNumberGenerator();
				final ServerController receiver = new ServerController(new GameController(strategy), client);
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

		public ServerController(GameController gameController, Socket socket) {
			this.gameEngine = gameController;
			try {
				this.dataInputStream = new DataInputStream(socket.getInputStream());
				this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendToAll(String message) {
			backgroundMessageClients.forEach(c -> {
				try {
					c.writeUTF(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
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
					ObjectMapper objectMapper = new ObjectMapper();
					String jsonResult;
					switch (messageType) {
						case CONNECTION:
//							jsonResult = objectMapper.writeValueAsString(gameRoomList);
//							dataOutputStream.writeUTF(jsonResult);
							break;
						case CREATE_ROOM:
							final GameRoom newGameRoom = GameRoomMaker.make(gameRoomList, value);
							gameRoomList.add(newGameRoom);
							jsonResult = objectMapper.writeValueAsString(newGameRoom);
							dataOutputStream.writeUTF(jsonResult);
							break;
						case JOIN:
							if (value != null) {
								final String[] clientSendValues = value.split(":");
								final long gameRoomId = Long.valueOf(clientSendValues[0]);
								final String role = clientSendValues[4];
								final String userId = clientSendValues[2];
								final User joinedUser = new User(userId, new Role(RoleType.valueOf(role)));

								GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
										(Collectors.toList()).get(0);

								final ErrorMessage errorMessage = new ErrorMessage();
								String errorMessageJson;
								if (gameRoom.getUsers().stream().filter(u -> u.getUserId().equals(userId)).count() >
										0) {
									errorMessage.setType(ErrorType.DUPLICATE_USER_ID);
									errorMessageJson = objectMapper.writeValueAsString("[ " + userId + " ]는 " +
											errorMessage);
								} else {
									gameRoom.getUsers().add(joinedUser);
									errorMessageJson = objectMapper.writeValueAsString(errorMessage);
								}
								dataOutputStream.writeUTF(errorMessageJson);
							}
							break;
						case GET_ROOM_LIST:
							jsonResult = objectMapper.writeValueAsString(gameRoomList);
							dataOutputStream.writeUTF(jsonResult);
							break;
						case READY:
							sendToAll("게임이 준비중 입니다. 게임 준비를 선택해 주세요.");
							break;
						case START:
							gameEngine.generateNum();
							break;
						case GUESS_NUM:
							try {
								gameEngine.guess(value);
								Result result = gameEngine.checkNumber(value);
								Score score = ScoreCalculator.calculateScore(3, result);

								ResultDto resultDto = new ResultDto(result, null, null, score, null);

								jsonResult = objectMapper.writeValueAsString(resultDto);

								dataOutputStream.writeUTF(jsonResult);
							} catch (IllegalArgumentException e) {

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