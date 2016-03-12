package kr.co.leehana.controller;

import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Setting;
import kr.co.leehana.service.GameRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class NumberGenerator implements GenerationNumberStrategy {

	private final GameRoomService gameRoomService;

	@Autowired
	public NumberGenerator(GameRoomService gameRoomService) {
		this.gameRoomService = gameRoomService;
	}

	@Override
	public String generateRandomNumber(final Setting setting) {
		Set<String> randomNumbers = new LinkedHashSet<>();

		while (randomNumbers.size() < setting.getGenerationNumberCount()) {
			int rNumber = (int) (Math.random() * 10);
			randomNumbers.add(String.valueOf(rNumber));
		}
		return String.join("", randomNumbers);
	}

	@Override
	public Integer generateRoomNumber() {
		final List<GameRoom> gameRoomList = gameRoomService.getAll();
		Integer newNumber = -1;
		if (gameRoomList == null || gameRoomList.size() == 0) {
			newNumber = 1;
		} else {
			GameRoom minNumberGameRoom = findMinNumberGameRoom(gameRoomList);
			if (minNumberGameRoom.getRoomNumber() > 1) {
				newNumber = 1;
			}

			GameRoom maxNumberGameRoom = findMaxNumberGameRoom(gameRoomList);
			for (int i = 2; i < maxNumberGameRoom.getRoomNumber(); i++) {
				final int tempNumber = i;
				if (gameRoomList.stream().filter(g -> g.getRoomNumber() == tempNumber).count() == 0) {
					newNumber = tempNumber;
					break;
				}
			}

			if (newNumber == -1) {
				newNumber = maxNumberGameRoom.getRoomNumber() + 1;
			}
		}
		return newNumber;
	}

	private GameRoom findMaxNumberGameRoom(List<GameRoom> gameRoomList) {
		final Comparator<GameRoom> comparator = (g1, g2) -> Integer.compare(g1.getRoomNumber(), g2.getRoomNumber());
		return gameRoomList.stream().max(comparator).get();
	}

	private GameRoom findMinNumberGameRoom(List<GameRoom> gameRoomList) {
		final Comparator<GameRoom> comparator = (g1, g2) -> Integer.compare(g1.getRoomNumber(), g2.getRoomNumber());
		return gameRoomList.stream().min(comparator).get();
	}
}