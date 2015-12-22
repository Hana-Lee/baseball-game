package com.eyeq.jhs;

import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.type.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Hana Lee
 * @since 2015-12-18 21:02
 */
class ServerReceiver extends Thread {

	private BaseballGameEngine gameEngine;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	public ServerReceiver(BaseballGameEngine baseballGameEngine, Socket socket) {
		this.gameEngine = baseballGameEngine;
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

				String messageTypeStringValue = null;
				String value = null;
				MessageType messageType;
				if (clientMsg.contains(",")) {
					messageTypeStringValue = clientMsg.split(",")[0];
					value = clientMsg.split(",")[1];
				} else {
					messageTypeStringValue = clientMsg;
				}
				messageType = MessageType.valueOf(messageTypeStringValue);

				switch (messageType) {
					case START:
						gameEngine.generateNum();
						break;
					case GUESS_NUM:
						try {
							gameEngine.guess(value);
							Result result = gameEngine.checkNumber(value);

							ObjectMapper objectMapper = new ObjectMapper();
							String jsonResult = objectMapper.writeValueAsString(result);

							dataOutputStream.writeUTF(jsonResult);
						} catch (IllegalArgumentException e) {

						}
						break;
					case GET_SCORE:
						// 실제 점수 계산하는 로직을 넣을것.
						dataOutputStream.writeUTF("900");
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
