package kr.co.leehana.exception;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:51
 */
public class PlayerNotFoundException extends RuntimeException {

	private Long id;
	private String email;

	public PlayerNotFoundException(Long id) {
		this.id = id;
	}

	public PlayerNotFoundException(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}
}

