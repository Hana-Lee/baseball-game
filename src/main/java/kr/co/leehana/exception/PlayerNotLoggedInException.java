package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-02-16 21:22
 */
public class PlayerNotLoggedInException extends RuntimeException {

	@Getter
	private String errorCode = "player.not.logged.in.exception";

	public PlayerNotLoggedInException(String message) {
		super(message);
	}
}
