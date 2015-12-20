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
class Receiver extends Thread {

	private BaseballGameEngine gameEngine;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	public Receiver(BaseballGameEngine baseballGameEngine, Socket socket) {
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

//							StringBuffer sb = new StringBuffer();
//							sb.append(MessageType.RESULT.getValue());
//							sb.append(",");
//							sb.append(MessageType.BALL.getValue());
//							sb.append(":");
//							sb.append(result.getBallCount());
//							sb.append(",");
//							sb.append(MessageType.STRIKE.getValue());
//							sb.append(":");
//							sb.append(result.getStrikeCount());
//
//							if (result.getSolve()) {
//								sb.append(",");
//								sb.append(MessageType.RESOLVED.getValue());
//								sb.append(",");
//								sb.append(MessageType.SCORE.getValue());
//								sb.append(":");
//								sb.append(Score.calculateScore(gameEngine.getNthGame(), result));
//							}
//							dataOutputStream.writeUTF(sb.toString());
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
