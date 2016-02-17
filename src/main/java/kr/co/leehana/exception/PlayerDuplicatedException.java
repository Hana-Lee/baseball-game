package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:48
 */
public class PlayerDuplicatedException extends RuntimeException {

	@Getter
	private String errorCode = "duplicated.email.exception";

	public PlayerDuplicatedException(String message) {
		super(message);
	}
}
