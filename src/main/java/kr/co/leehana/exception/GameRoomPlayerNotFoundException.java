package kr.co.leehana.exception;

import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-02-12 20:12
 */
public class GameRoomPlayerNotFoundException extends RuntimeException {

	private GameRoom gameRoom;

	private Player player;

	@Getter
	private String errorCode = "gameroom.player.not.found.exception";

	public GameRoomPlayerNotFoundException(GameRoom gameRoom, Player player) {
		this.gameRoom = gameRoom;
		this.player = player;
	}

	public String getMessage() {
		return "Game room '" + gameRoom.getName() + "' has not player '" + player.getNickname() + "'";
	}
}
