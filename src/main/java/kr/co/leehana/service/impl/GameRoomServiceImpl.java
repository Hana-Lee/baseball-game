package kr.co.leehana.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.enums.Status;
import kr.co.leehana.exception.GameRoomNotFoundException;
import kr.co.leehana.exception.OwnerDuplicatedException;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.repository.GameRoomRepository;
import kr.co.leehana.service.GameRoomService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:48
 */
@Service
@Transactional
@Slf4j
public class GameRoomServiceImpl implements GameRoomService {

	private final GameRoomRepository gameRoomRepository;
	private final ModelMapper modelMapper;
	private final MessageSendingOperations<String> messageSendingOperations;
	private final ObjectMapper objectMapper;

	@Autowired
	public GameRoomServiceImpl(GameRoomRepository gameRoomRepository, ModelMapper modelMapper,
	                           MessageSendingOperations<String> messageSendingOperations, ObjectMapper objectMapper) {
		this.gameRoomRepository = gameRoomRepository;
		this.modelMapper = modelMapper;
		this.messageSendingOperations = messageSendingOperations;
		this.objectMapper = objectMapper;
	}

	@Override
	public GameRoom create(final GameRoomDto.Create createDto) throws JsonProcessingException {
		final GameRoom gameRoom = modelMapper.map(createDto, GameRoom.class);

		if (gameRoomRepository.findByOwner(createDto.getOwner()) != null) {
			log.error("owner duplicated exception. {} : {}", createDto.getOwner().getId(), createDto.getOwner()
					.getNickname());
			throw new OwnerDuplicatedException(createDto.getOwner());
		}

		fillInitData(gameRoom);

		final GameRoom createdGameRoom = gameRoomRepository.save(gameRoom);
		sendNewGameRoomNotification(createdGameRoom);
		return createdGameRoom;
	}

	private void sendNewGameRoomNotification(GameRoom newGameRoom) throws JsonProcessingException {
		String topic = "/topic/gameroom-created";
		GameRoomDto.Message message = new GameRoomDto.Message();
		message.setOperation("insert");
		message.setData(newGameRoom);
		messageSendingOperations.convertAndSend(topic, message);
	}

	private void fillInitData(final GameRoom gameRoom) {
		gameRoom.setStatus(Status.NORMAL);

		gameRoom.getPlayers().add(gameRoom.getOwner());
		gameRoom.getPlayerRankMap().put(1, gameRoom.getOwner());

		final Date now = new Date();
		gameRoom.setCreated(now);
		gameRoom.setUpdated(now);
	}

	@Override
	public GameRoom getById(final Long id) {
		final GameRoom gameRoom = gameRoomRepository.findOne(id);
		if (gameRoom == null) {
			throw new GameRoomNotFoundException("[" + id + "] 에 해당하는 게임룸이 없습니다.");
		}

		return gameRoom;
	}

	@Override
	public List<GameRoom> getAll() {
		return gameRoomRepository.findAll();
	}

	@Override
	public Page<GameRoom> getAll(Pageable pageable) {
		return gameRoomRepository.findAll(pageable);
	}

	@Override
	public GameRoom update(Long id, GameRoomDto.Update updateDto) {
		final GameRoom gameRoom = getById(id);

		if (updateDto.getName() != null && !updateDto.getName().isEmpty()) {
			gameRoom.setName(updateDto.getName());
		}

		if (updateDto.getPlayers() != null) {
			gameRoom.getPlayers().clear();
			gameRoom.setPlayers(updateDto.getPlayers());
		}

		if (updateDto.getSetting() != null) {
			gameRoom.setSetting(updateDto.getSetting());
		}

		if (updateDto.getGameCount() != null) {
			updateDto.setGameCount(updateDto.getGameCount());
		}

		if (updateDto.getPlayerRankMap() != null) {
			gameRoom.getPlayerRankMap().clear();
			gameRoom.setPlayerRankMap(updateDto.getPlayerRankMap());
		}

		gameRoom.setUpdated(new Date());

		return gameRoomRepository.save(gameRoom);
	}

	@Override
	public void delete(Long id) {
		gameRoomRepository.delete(id);
	}
}
