package kr.co.leehana.exception;

import kr.co.leehana.model.Player;
import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:55
 */
public class OwnerDuplicatedException extends RuntimeException {

	@Getter
	private Player owner;

	public OwnerDuplicatedException(Player owner) {
		this.owner = owner;
	}
}
