package kr.co.leehana.exception;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:51
 */
public class AccountNotFoundException extends RuntimeException {

	private long id;

	public AccountNotFoundException(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}

