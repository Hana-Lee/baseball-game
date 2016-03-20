package kr.co.leehana.controller;

import kr.co.leehana.dto.ChatDto;
import kr.co.leehana.dto.MessagingDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.enums.Status;
import kr.co.leehana.model.GameNumber;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.GuessNumberComparedResult;
import kr.co.leehana.model.Level;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Rank;
import kr.co.leehana.model.Score;
import kr.co.leehana.service.ChatService;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import kr.co.leehana.util.ScoreCalculator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hana Lee
 * @since 2016-02-21 21:31
 */
@RestController
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
	public MessagingDto readyNotificationToGameRoom(@DestinationVariable Long id, Principal principal) {
		final GameRoom gameRoom = gameRoomService.getById(id);

		updateGameRoomStatus(gameRoom);
		gameRoomService.update(gameRoom);

		final MessagingDto dto = new MessagingDto();
		dto.setData(gameRoom);
		dto.setOperation("playerReadyStatusUpdated");
		return dto;
	}

	@MessageMapping(value = {"/gameroom/{id}/player-guess-number"})
	@SendToUser(value = {"/topic/gameroom/{id}/progress/updated", "/topic/player/updated"}, broadcast = false)
	public MessagingDto inputGuessNumber(@DestinationVariable Long id, @Payload @Valid PlayerDto.Update updateDto,
	                                     Principal principal) throws Exception {
		updateDto.setGuessNumber(updateDto.getGuessNumber().replaceAll(" ", ""));

		updateDto.setInputCount(updateDto.getInputCount() + 1);

		final GameRoom gameRoom = gameRoomService.getById(id);
		final Player player = playerService.getByEmail(principal.getName());
		player.setInputCount(updateDto.getInputCount());

		GuessNumberComparedResult result = gameController.compareNumber(gameRoom.getGameNumber().getValue(), updateDto
				.getGuessNumber());

		// TODO 중복 입력 제거 할 것.
		updateDto.setResult(result);
		player.setResult(result);

		String guessResultMessage = gameController.makeGuessResultMessage(result, updateDto);

		if (gameController.isGameOver(result) || Objects.equals(player.getInputCount(), gameRoom.getSetting()
				.getLimitGuessInputCount())) {
			updateDto.setStatus(Status.GAME_OVER);
			updateDto.setGameOverTime(new Date());

			final Rank playerRank = new Rank();
			Long rankValue = gameRoom.getPlayers().stream().filter(p -> p.getRank() != null && p.getRank().getValue()
					> 0).count() + 1;
			playerRank.setValue(rankValue.intValue());

			// TODO 중복 입력 제거 할 것.
			updateDto.setRank(playerRank);
			player.setRank(playerRank);

			Score score = ScoreCalculator.calculation(player, gameRoom);
			updateDto.setScore(score);

			final Integer totalScore = player.getTotalScore().getValue() + updateDto.getScore().getValue();
			updateDto.getTotalScore().setValue(totalScore);
		}

		final Player updatedPlayer = playerService.updateByEmail(principal.getName(), updateDto);

		Map<String, String> messageData = new HashMap<>();
		messageData.put("message", guessResultMessage);
		messageData.put("type", "alert");
		MessagingDto dto = new MessagingDto();
		dto.setObject(modelMapper.map(updatedPlayer, PlayerDto.Response.class));
		dto.setObjectOperation("playerGuessNumber");
		dto.setId(String.valueOf(id));
		dto.setData(messageData);
		dto.setOperation("insert");

		return dto;
	}

	@MessageMapping(value = {"/gameroom/{id}/player-input-count-notification"})
	@SendTo(value = {"/topic/gameroom/{id}/progress/updated"})
	public MessagingDto playerInputCountNotification(@DestinationVariable Long id, @Payload Map<String, String>
			clientIdPayload, Principal principal) {
		final Player player = playerService.getByEmail(principal.getName());

		Map<String, String> messageData = new HashMap<>();
		messageData.put("message", player.getNickname() + "님 " + (player.getInputCount() + 1) + "번째 입력중");
		messageData.put("type", "focus");
		MessagingDto dto = new MessagingDto();
		dto.setClientId(clientIdPayload.get("clientId"));
		dto.setId(String.valueOf(id));
		dto.setData(messageData);
		dto.setOperation("insert");

		return dto;
	}

	@MessageMapping(value = {"/gameroom/{id}/player-game-over-notification"})
	@SendTo(value = {"/topic/gameroom/{id}/progress/updated", "/topic/gameroom/{id}/updated"})
	public MessagingDto playerGameOverNotification(@DestinationVariable Long id, @Payload Map<String, String>
			clientIdPayload, Principal principal) {
		final Player player = playerService.getByEmail(principal.getName());
		final GameRoom gameRoom = gameRoomService.getById(id);
		if (gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getStatus(), Status.GAME_OVER)).count() ==
				gameRoom.getPlayers().size()) {
			gameRoom.setStatus(Status.GAME_END);
			gameRoom.getPlayers().forEach(p -> {
				p.setStatus(Status.READY_BEFORE);
				p.setInputCount(0);
				p.setGuessNumber(null);
				// TODO 스코어, 랭킹, 전적
				updatePlayerTotalRank(p);
				updatePlayerMatchRecord(p);
				updatePlayerLevel(p);
			});
			gameRoom.setGameNumber(null);
			gameRoomService.update(gameRoom);
		}

		Map<String, String> messageData = new HashMap<>();
		messageData.put("message", player.getNickname() + "님이 숫자를 맞췄습니다 (" + player.getInputCount() + "/" + gameRoom
				.getSetting().getLimitGuessInputCount() + ")");
		messageData.put("type", "focus");
		MessagingDto dto = new MessagingDto();
		dto.setObject(gameRoom);
		dto.setObjectOperation("playerGameOverUpdate");
		dto.setClientId(clientIdPayload.get("clientId"));
		dto.setId(String.valueOf(id));
		dto.setData(messageData);
		dto.setOperation("insert");

		return dto;
	}

	/**
	 * <p>플레이어의 레벨을 업데이트 한다</p>
	 * <p>2레벨 -> 3레벨 : 200점 필요</p>
	 * <p>3레벨 -> 4레벨 : 300점 필요</p>
	 * <p>99레벨 -> 100레벨 : 9900점 필요</p>
	 * <p>100레벨 -> 101레벨 : 10000점 필요</p>
	 *
	 * @param player {@code Player} 레벨을 업데이트 할 플레이어
	 */
	private void updatePlayerLevel(Player player) {
		final Level currentLevel = player.getLevel();
		if (player.getTotalScore().getValue() > 0) {
			boolean levelUpDone = false;
			int baseLevelScoreValue = 100;
			Integer levelValue = currentLevel.getValue();
			if (levelValue > 1) {
				for (int i = 2; i <= levelValue; i++) {
					baseLevelScoreValue += 100 * i;
				}
			}

			while (!levelUpDone) {
				int scoreValue = player.getTotalScore().getValue() - baseLevelScoreValue;
				if (scoreValue < 0) {
					levelUpDone = true;
				} else {
					levelValue += 1;
					baseLevelScoreValue += levelValue * 100;
				}
			}

			if (currentLevel.getValue() < levelValue) {
				player.getLevel().setValue(levelValue);
			}
		}
	}

	/**
	 * <p>플레이어의 전적을 업데이트 한다</p>
	 * <p>폐배의 조건은 숫자를 끝까지 못맞췄을 경우이다</p>
	 *
	 * @param player {@code Player} 전적을 업데이트할 플레이어
	 */
	private void updatePlayerMatchRecord(Player player) {
		final Integer totalGameCount = player.getMatchRecord().getTotalGame().getCount();
		player.getMatchRecord().getTotalGame().setCount(totalGameCount + 1);
		if (player.getResult() == null || !player.getResult().getSettlement().getSolved()) {
			final Integer loseCount = player.getMatchRecord().getLose().getCount();
			player.getMatchRecord().getLose().setCount(loseCount + 1);
		} else if (player.getResult().getSettlement().getSolved()) {
			final Integer winCount = player.getMatchRecord().getWin().getCount();
			player.getMatchRecord().getWin().setCount(winCount + 1);
		}
	}

	/**
	 * <p>플레이어의 랭킹을 수정하는 메소드</p>
	 * <p>랭킹은 변경되지 않을 수 도 있다</p>
	 *
	 * @param player {@code Player} 랭킹을 변경할 플레이어
	 * @return 랭킹 값이 변경 되면 {@code true} 가 반환 된다
	 */
	private boolean updatePlayerTotalRank(final Player player) {
		final List<Player> allPlayers = playerService.getAll();

		final Long greaterThanScoreCount = allPlayers.stream().filter(p -> p.getTotalScore().getValue() > player
				.getTotalScore().getValue()).count();

		final Long sameScorePlayerCount = allPlayers.stream().filter(p -> Objects.equals(p.getTotalScore().getValue(),
				player.getTotalScore().getValue())).count();

		Integer totalRankValue = greaterThanScoreCount.intValue() + 1;
		if (greaterThanScoreCount == 0 && sameScorePlayerCount == 0) { // 높은 플레이어가 없고 같은 플레이어도 없으면..
//			totalRankValue = 1;
		} else if (greaterThanScoreCount > 0 && sameScorePlayerCount == 0) { // 높은 플레이어는 있고 같은 플레이어가 없으면..
//			totalRankValue = greaterThanScoreCount.intValue() + 1;
		} else if (greaterThanScoreCount > 0 && sameScorePlayerCount > 0) { // 높은 플레이어는 있고 같은 플레이어도 있으면..
			final Long lessThanGameOverTimeCount = allPlayers.stream().filter(p -> Objects.equals(p.getTotalScore()
					.getValue(), player.getTotalScore().getValue()) && p.getGameOverTime().getTime() < player
					.getGameOverTime().getTime()).count();
			if (lessThanGameOverTimeCount > 0) {
				totalRankValue += lessThanGameOverTimeCount.intValue();
			}
		} else if (greaterThanScoreCount == 0 && sameScorePlayerCount > 0) { // 높은 플레이어는 없고 같은 플레이어도 있으면..
			final Long lessThanGameOverTimeCount = allPlayers.stream().filter(p -> Objects.equals(p.getTotalScore()
					.getValue(), player.getTotalScore().getValue()) && p.getGameOverTime().getTime() < player
					.getGameOverTime().getTime()).count();
			if (lessThanGameOverTimeCount > 0) {
				totalRankValue += lessThanGameOverTimeCount.intValue();
			}
		}

		final Integer originalTotalRankValue = player.getTotalScore().getValue();
		if (!Objects.equals(originalTotalRankValue, totalRankValue)) { // 현재 랭킹과 다르면..
			player.getTotalRank().setValue(totalRankValue);

			final Integer finalTotalRankValue = totalRankValue;
			// 현재 등수와 동일한 플레이어 또는 이전 등수보다 작고 현재 등수보다 높은 플레이어를 찾아 +1 씩 등수를 조절한다
			allPlayers.stream().filter(p -> Objects.equals(p.getTotalRank().getValue(), player.getTotalRank().getValue
					()) || (originalTotalRankValue > p.getTotalRank().getValue() && finalTotalRankValue < p
					.getTotalRank().getValue())).forEach(p -> p.getTotalRank().setValue(p.getTotalRank().getValue() +
					1));

			return true;
		}

		return false;
	}

	@MessageMapping(value = {"/gameroom/{id}/game-end-notification"})
	@SendTo(value = {"/topic/gameroom/{id}/progress/updated", "/topic/gameroom/{id}/updated"})
	public MessagingDto gameEndNotification(@DestinationVariable Long id) {
		final GameRoom gameRoom = gameRoomService.getById(id);
		gameRoom.setStatus(Status.GAME_END);

//		gameController.makePlayerRankMap(gameRoom);
//
//		gameRoom.getPlayers().stream().forEach(p -> {
//			p.setStatus(Status.READY_BEFORE);
//			p.setInputCount(0);
//			p.setGuessNumber(null);
//			p.setGameOverTime(null);
//			// 모든 게임이 끝날때까지 기다려야 전적이 기록된다.
//			p.getMatchRecord().getLose().setCount(0);
//			p.getMatchRecord().getWin().setCount(1);
//			p.getMatchRecord().getTotalGame().setCount(1);
//		});

		gameRoomService.update(gameRoom);

		final Map<String, String> messageData = new HashMap<>();
		messageData.put("message", "게임이 종료 되었습니다");
		messageData.put("type", "alert");

		final MessagingDto dto = new MessagingDto();
		dto.setObject(gameRoom);
		dto.setObjectOperation("gameRoomGameEnd");
		dto.setData(messageData);
		dto.setId(String.valueOf(id));
		dto.setOperation("insert");

		return dto;
	}

	private void updateGameRoomStatus(GameRoom gameRoom) {
		if (isAllPlayersReadyDone(gameRoom)) {
			gameRoom.setStatus(Status.RUNNING);
			gameRoom.setGameCount(gameRoom.getGameCount() + 1);

			if (gameRoom.getGameNumber() == null || StringUtils.isBlank(gameRoom.getGameNumber().getValue())) {
				gameRoom.setGameNumber(new GameNumber(gameController.generateNumber(gameRoom.getSetting())));
			}
		} else {
			gameRoom.setStatus(Status.NORMAL);
		}

		if (!gameRoom.getPlayerRankMap().isEmpty()) {
			gameRoom.getPlayerRankMap().clear();
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
