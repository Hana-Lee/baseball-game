package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:38
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@ToString
public class GameRoom {

	@Setter(AccessLevel.NONE)
	private long id;
	private String name;
	private int limit = 5;
	private User owner;
	private Set<User> users = new LinkedHashSet<>();
	private Setting setting;
	private int gameCount = 0;
	private String generationNumbers;

	public GameRoom(long id, String name, User owner, int limit, Setting setting) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.limit = limit;
		this.setting = setting;

		init(this.owner);
	}

	public GameRoom(long id, String name, int limit, Setting setting) {
		this.id = id;
		this.name = name;
		this.limit = limit;
		this.setting = setting;
	}

	private void init(User owner) {
		getUsers().add(owner);
	}
}
