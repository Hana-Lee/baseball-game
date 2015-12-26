package com.eyeq.jhs.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:38
 */
@Data
public class GameRoom {

	private long id;
	private String name;
	private int limit;
	private Set<User> users = new HashSet<>();

	public GameRoom(long id, String name, int limit) {
		this.id = id;
		this.name = name;
		this.limit = limit;
	}
}
