package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:51
 */
public class PlayerNotFoundException extends RuntimeException {

	@Getter
	private String errorCode = "player.not.found.exception";

	public PlayerNotFoundException() {
		super("email 혹은 비번을 확인해주세요");
	}

	public PlayerNotFoundException(String message) {
		super(message);
	}
}

