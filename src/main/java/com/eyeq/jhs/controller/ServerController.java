package com.eyeq.jhs.controller;

import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.model.ResultDto;
import com.eyeq.jhs.model.Score;
import com.eyeq.jhs.model.ScoreCalculator;
import com.eyeq.jhs.model.User;
import com.eyeq.jhs.type.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hana Lee
 * @since 2015-12-18 21:02
 */
class ServerController extends Thread {

	private GameController gameEngine;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private List<GameRoom> gameRoomList;

	public ServerController(GameController gameController, Socket socket, List<GameRoom> gameRoomList) {
		this.gameEngine = gameController;
		this.gameRoomList = gameRoomList;
		try {
			this.dataInputStream = new DataInputStream(socket.getInputStream());
			this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						jsonResult = objectMapper.writeValueAsString(gameRoomList);
						dataOutputStream.writeUTF(jsonResult);
						break;
					case JOIN:
						if (value != null) {
							final String[] clientSendValues = value.split(":");
							final long gameRoomId = Long.valueOf(clientSendValues[0]);
							final User joinedUser = new User(clientSendValues[2]);
							GameRoom gameRoom = gameRoomList.stream().filter(r -> r.getId() == gameRoomId).collect
									(Collectors.toList()).get(0);
							gameRoom.getUsers().add(joinedUser);
						}
						break;
					case GET_ROOM_LIST:
						jsonResult = objectMapper.writeValueAsString(gameRoomList);
						dataOutputStream.writeUTF(jsonResult);
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
