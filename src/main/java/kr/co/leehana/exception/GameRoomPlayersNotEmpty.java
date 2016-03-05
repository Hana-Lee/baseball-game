package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-03-05 16:17
 */
public class GameRoomPlayersNotEmpty extends RuntimeException {

	@Getter
	private String errorCode = "gameroom.player.list.not.empty.exception";

	public GameRoomPlayersNotEmpty(String message) {
		super(message);
	}
}
