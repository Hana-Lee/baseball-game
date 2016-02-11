package kr.co.leehana.exception;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-02-11 20:20
 */
public class OwnerChangeException extends RuntimeException {

	@Getter
	private String message = "Owner change exception";

	@Getter
	private String errorCode = "owner.change.exception";

	public OwnerChangeException(String message) {
		this.message = message;
	}
}
