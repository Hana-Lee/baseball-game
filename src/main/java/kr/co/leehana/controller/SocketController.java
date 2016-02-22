package kr.co.leehana.controller;

import kr.co.leehana.model.ChatMessage;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;

/**
 * @author Hana Lee
 * @since 2016-02-21 21:31
 */
@Controller
public class SocketController {

	@Autowired
	private GameRoomService gameRoomService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	private ModelMapper modelMapper;

	@MessageMapping(value = {"/chat"})
	@SendTo(value = {"/topic/chat/message"})
	public ChatMessage chat(ChatMessage message) {
		return message;
	}
}
