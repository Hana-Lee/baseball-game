package com.eyeq.jhs.factory;

import com.eyeq.jhs.model.GameRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-12-26 20:17
 */
public class GameRoomMaker {

	public static List<GameRoom> make() {
		final int LIMIT_USER_COUNT = 10;

		List<GameRoom> gameRooms = new ArrayList<>();
		gameRooms.add(new GameRoom(1, "에메랄드", LIMIT_USER_COUNT));
		gameRooms.add(new GameRoom(2, "다이아몬드", LIMIT_USER_COUNT));
		gameRooms.add(new GameRoom(3, "사파이어", LIMIT_USER_COUNT));

		return gameRooms;
	}
}
