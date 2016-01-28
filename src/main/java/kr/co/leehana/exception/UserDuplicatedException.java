package kr.co.leehana.exception;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:48
 */
public class UserDuplicatedException extends RuntimeException {

	private String email;

	public UserDuplicatedException(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
