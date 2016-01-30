package kr.co.leehana.exception;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:48
 */
public class PlayerDuplicatedException extends RuntimeException {

	private String email;

	public PlayerDuplicatedException(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
