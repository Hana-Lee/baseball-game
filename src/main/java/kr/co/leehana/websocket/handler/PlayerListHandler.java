package kr.co.leehana.websocket.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author Hana Lee
 * @since 2016-02-21 19:45
 */
public class PlayerListHandler extends TextWebSocketHandler {

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(message);
		super.handleTextMessage(session, message);
	}
}
