package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-03-16 14:15
 */
public class GameRoomUpdateFieldAllEmptyException extends RuntimeException {

	@Getter
	private String errorCode = "gameroom.all.field.empty.exception";

	public GameRoomUpdateFieldAllEmptyException(String s) {
		super(s);
	}
}
