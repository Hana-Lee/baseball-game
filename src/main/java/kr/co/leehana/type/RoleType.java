package kr.co.leehana.type;

/**
 * @author Hana Lee
 * @since 2015-12-27 05:11
 */
public enum RoleType {

	USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

	private String roleName;

	RoleType(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}
