package kr.co.leehana.exception;

/**
 * @author Hana Lee
 * @since 2016-02-03 18:24
 */
public class GameRoomNotFoundException extends RuntimeException {

	private Long id;

	public GameRoomNotFoundException(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
