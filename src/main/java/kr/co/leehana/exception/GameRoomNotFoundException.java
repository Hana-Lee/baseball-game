package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-02-03 18:24
 */
public class GameRoomNotFoundException extends RuntimeException {

	@Getter
	private String message;

	@Getter
	private String errorCode = "gameroom.not.found.exception";

	public GameRoomNotFoundException(String message) {
		this.message = message;
	}
}
