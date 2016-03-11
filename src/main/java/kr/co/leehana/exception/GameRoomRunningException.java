package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-03-11 21:37
 */
public class GameRoomRunningException extends RuntimeException {

	@Getter
	private String errorCode = "gameroom.running.exception";

	public GameRoomRunningException(String message) {
		super(message);
	}
}
