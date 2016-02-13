package kr.co.leehana.exception;

import kr.co.leehana.enums.GameRole;
import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2016-02-09 23:45
 */
public class GameRoleDuplicatedException extends RuntimeException {

	@Getter
	private GameRole gameRole;

	@Getter
	private String message = "Defender must not be duplicated";

	@Getter
	private String errorCode = "duplicated.gameRole.exception";

	public GameRoleDuplicatedException(GameRole gameRole) {
		this.gameRole = gameRole;
	}
}
