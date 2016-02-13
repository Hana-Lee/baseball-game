package kr.co.leehana.enums;

/**
 * @author Hana Lee
 * @since 2015-12-27 05:11
 */
public enum PlayerRole {

	USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

	private String roleName;

	PlayerRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}
