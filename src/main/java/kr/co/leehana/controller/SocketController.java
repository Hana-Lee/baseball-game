package kr.co.leehana.controller;

import kr.co.leehana.dto.ChatDto;
import kr.co.leehana.dto.MessagingDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.enums.Status;
import kr.co.leehana.model.GameNumber;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.GuessNumberComparedResult;
import kr.co.leehana.model.Player;
import kr.co.leehana.service.ChatService;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hana Lee
 * @since 2016-02-21 21:31
 */
@Controller
@Slf4j
public class SocketController {

	@Autowired
	private GameRoomService gameRoomService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private ChatService chatService;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private GenerationNumberStrategy generationNumberStrategy;

	@Autowired
	private GameController gameController;

	@MessageMapping(value = {"/chat"})
	@SendTo(value = {"/topic/chat"})
	public ChatDto.Message chat(ChatDto.Message message) {
		saveChatMessage(message);
		return message;
	}

	@MessageMapping(value = {"/chat/gameroom/{id}"})
	@SendTo(value = {"/topic/chat/gameroom/{id}"})
	public ChatDto.Message gameRoomChat(@DestinationVariable Long id, ChatDto.Message message) {
		log.debug("Chat destination game room id : {}", id);

		saveChatMessage(message);
		return message;
	}

	@MessageMapping(value = {"/player/login", "/player/logout"})
	@SendTo(value = {"/topic/player/list/updated"})
	public PlayerDto.Message playerLoggedInOut(Map<String, String> params) {
		return createCurrentPlayerInfoMessage(params.get("email"), params.get("operation"));
	}

	@MessageMapping(value = {"/player/ready/{id}"})
	@SendTo(value = {"/topic/gameroom/{id}/progress/updated"})
	public MessagingDto gameRoomProgressUpdate(@DestinationVariable Long id, Principal principal) {
		final Player player = playerService.getByEmail(principal.getName());
		player.setInputCount(0);
		player.setGuessNumber(null);
		String message = "";
		String type = "";
		if (Objects.equals(player.getStatus(), Status.READY_DONE)) {
			message = player.getNickname() + "님 준비 완료!";
			type = "normal";
		} else if (Objects.equals(player.getStatus(), Status.READY_BEFORE)) {
			message = player.getNickname() + "님 준비 취소!";
			type = "alert";
		}

		Map<String, String> messageData = new HashMap<>();
		messageData.put("message", message);
		messageData.put("type", type);
		MessagingDto dto = new MessagingDto();
		dto.setId(String.valueOf(id));
		dto.setData(messageData);
		dto.setOperation("insert");
		return dto;
	}

	@MessageMapping(value = {"/player/ready/{id}/gameroom/notification"})
	@SendTo(value = {"/topic/gameroom/{id}/player-ready-status-updated"})
	public MessagingDto readyNotificationToGameRoom(@DestinationVariable Long id) {
		final GameRoom gameRoom = gameRoomService.getById(id);

		updateGameRoomStatus(gameRoom);
		gameRoomService.update(gameRoom);

		final MessagingDto dto = new MessagingDto();
		dto.setData(gameRoom);
		dto.setOperation("update");
		return dto;
	}

	@MessageMapping(value = {"/gameroom/{id}/player-guess-number"})
	@SendToUser(value = {"/topic/gameroom/{id}/progress/updated", "/topic/player/updated"}, broadcast = false)
	public MessagingDto inputGuessNumber(@DestinationVariable Long id, @Payload @Valid PlayerDto.Update updateDto,
	                                     Principal principal) throws Exception {
		updateDto.setGuessNumber(updateDto.getGuessNumber().replaceAll(" ", ""));

		updateDto.setInputCount(updateDto.getInputCount() + 1);

		final GameRoom gameRoom = gameRoomService.getById(id);

		GuessNumberComparedResult result = gameController.compareNumber(gameRoom.getGameNumber().getValue(),
				updateDto.getGuessNumber());

		String guessResultMessage = gameController.makeGuessResultMessage(result, updateDto);

		if (gameController.isGameEnd(result)) {
			updateDto.setStatus(Status.GAME_OVER);
		}

		final Player updatedPlayer = playerService.updateByEmail(principal.getName(), updateDto);

		Map<String, String> messageData = new HashMap<>();
		messageData.put("message", guessResultMessage);
		messageData.put("type", "alert");
		MessagingDto dto = new MessagingDto();
		dto.setObject(modelMapper.map(updatedPlayer, PlayerDto.Response.class));
		dto.setObjectOperation("guessNumberPlayer");
		dto.setId(String.valueOf(id));
		dto.setData(messageData);
		dto.setOperation("insert");

		return dto;
	}

	/**
	 * {code @Valid} 어노테이션에 의해 검증 오류가 발생하면 처리 하는 핸들러
	 *
	 * @param exception MethodArgumentNotValidException Class 검증 오류
	 * @return 검증 오류의 메세지를 담은 객체
	 */
	@MessageExceptionHandler(value = {MethodArgumentNotValidException.class})
	@SendToUser(value = {"/topic/errors"}, broadcast = false)
	public MessagingDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		// TODO Exception Handler 들을 ControllerAdvice 로 모두 옮겨서 관리 할 것
		String message = null;
		if (exception.getBindingResult().getGlobalError() != null) {
			message = exception.getBindingResult().getGlobalError().getDefaultMessage();
		} else if (exception.getBindingResult().getFieldError() != null) {
			message = exception.getBindingResult().getFieldError().getDefaultMessage();
		}

		MessagingDto dto = new MessagingDto();
		dto.setErrorMessage(message);
		dto.setErrorCode(exception.getBindingResult().getGlobalError().getCode());
		return dto;
	}

	private void updateGameRoomStatus(GameRoom gameRoom) {
		if (isAllPlayersReadyDone(gameRoom)) {
			gameRoom.setStatus(Status.RUNNING);

			if (gameRoom.getGameNumber() == null || StringUtils.isBlank(gameRoom.getGameNumber().getValue())) {
				gameRoom.setGameNumber(new GameNumber(gameController.generateNumber(gameRoom.getSetting())));
			}
		} else {
			gameRoom.setStatus(Status.NORMAL);
		}

		updatePlayersStatus(gameRoom);
	}

	private void updatePlayersStatus(GameRoom gameRoom) {
		if (Objects.equals(gameRoom.getStatus(), Status.RUNNING)) {
			gameRoom.getPlayers().stream().forEach(p -> p.setStatus(Status.INPUT));
			gameRoom.getOwner().setStatus(Status.INPUT);
		}
	}

	private boolean isAllPlayersReadyDone(GameRoom gameRoom) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(Status.READY_DONE, p.getStatus())).count() ==
				gameRoom.getPlayers().size();
	}

	private void saveChatMessage(ChatDto.Message message) {
		chatService.create(message.getData());
	}

	private PlayerDto.Message createCurrentPlayerInfoMessage(String email, String operation) {
		PlayerDto.Message message = new PlayerDto.Message();
		message.setOperation(operation);
		message.setData(modelMapper.map(playerService.getByEmail(email), PlayerDto.Response.class));

		return message;
	}
}
