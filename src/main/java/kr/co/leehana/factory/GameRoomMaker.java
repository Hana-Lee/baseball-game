package kr.co.leehana.factory;

import kr.co.leehana.model.OldGameRoom;
import kr.co.leehana.model.OldUser;
import kr.co.leehana.model.Setting;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-12-26 20:17
 */
public class GameRoomMaker {

	public static OldGameRoom make(List<OldGameRoom> gameRoomList, String roomName, OldUser ownerUser) {
		final int LIMIT_USER_COUNT = 5;

		final Long maxId = gameRoomList.isEmpty() ? 1L : gameRoomList.stream().map(OldGameRoom::getId).reduce(Long::max)
				.get() + 1;
		return new OldGameRoom(maxId, roomName, ownerUser, LIMIT_USER_COUNT, new Setting());
	}
}
