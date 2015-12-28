package com.eyeq.jhs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:38
 */
@Data
@NoArgsConstructor
public class GameRoom {

	private long id;
	private String name;
	private int limit = 5;
	private User owner;
	private Set<User> users = new HashSet<>();

	public GameRoom(long id, String name, User owner, int limit) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.limit = limit;

		init(this.owner);
	}

	public GameRoom(long id, String name, int limit) {
		this.id = id;
		this.name = name;
		this.limit = limit;
	}

	private void init(User owner) {
		getUsers().add(owner);
	}
}
