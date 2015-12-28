package com.eyeq.jhs.factory;

import com.eyeq.jhs.model.GameRoom;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-12-26 20:17
 */
public class GameRoomMaker {

	public static GameRoom make(List<GameRoom> gameRoomList, String roomName) {
		final int LIMIT_USER_COUNT = 5;

		final Long maxId = gameRoomList.isEmpty() ? 1L : gameRoomList.stream().map(GameRoom::getId).reduce(Long::max)
				.get();
		return new GameRoom(maxId + 1, roomName, LIMIT_USER_COUNT);
	}
}
