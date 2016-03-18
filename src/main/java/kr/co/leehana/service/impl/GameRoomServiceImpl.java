package kr.co.leehana.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.enums.Enabled;
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

	@Autowired
	public GameRoomServiceImpl(GameRoomRepository gameRoomRepository, ModelMapper modelMapper) {
		this.gameRoomRepository = gameRoomRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public GameRoom create(final GameRoomDto.Create createDto) throws JsonProcessingException {
		final GameRoom gameRoom = modelMapper.map(createDto, GameRoom.class);

		if (gameRoomRepository.findOneByOwnerAndEnabled(createDto.getOwner(), Enabled.TRUE) != null) {
			log.error("owner duplicated exception. {} : {}", createDto.getOwner().getId(), createDto.getOwner()
					.getNickname());
			throw new OwnerDuplicatedException("[" + createDto.getOwner().getEmail() + "] 중복된 방장 입니다.");
		}

		fillInitData(gameRoom);

		return gameRoomRepository.save(gameRoom);
	}

	private void fillInitData(final GameRoom gameRoom) {
		gameRoom.setStatus(Status.NORMAL);

		gameRoom.getPlayers().add(gameRoom.getOwner());
		gameRoom.getPlayerRankMap().put(1, gameRoom.getOwner());

		final Date now = new Date();
		gameRoom.setCreated(now);
		gameRoom.setUpdated(now);

		gameRoom.setEnabled(Enabled.TRUE);
	}

	@Override
	public GameRoom getById(final Long id) {
		final GameRoom gameRoom = getByIdAndEnabled(id, Enabled.TRUE);
		if (gameRoom == null) {
			throw new GameRoomNotFoundException("[" + id + "] 에 해당하는 게임룸이 없습니다.");
		}

		return gameRoom;
	}

	@Override
	public GameRoom getByIdAndEnabled(Long id, Enabled enabled) {
		return gameRoomRepository.findOneByIdAndEnabled(id, enabled);
	}

	@Override
	public List<GameRoom> getAll() {
		return getAllByEnabled(Enabled.TRUE);
	}

	@Override
	public List<GameRoom> getAllByEnabled(Enabled enabled) {
		return gameRoomRepository.findAllByEnabled(enabled);
	}

	@Override
	public Page<GameRoom> getAll(Pageable pageable) {
		return getAllByEnabled(Enabled.TRUE, pageable);
	}

	@Override
	public Page<GameRoom> getAllByEnabled(Enabled enabled, Pageable pageable) {
		return gameRoomRepository.findAllByEnabled(enabled, pageable);
	}

	@Override
	public GameRoom update(Long id, GameRoomDto.Update updateDto) {
		final GameRoom gameRoom = getById(id);

		if (updateDto.getName() != null && !updateDto.getName().isEmpty()) {
			gameRoom.setName(updateDto.getName());
		}

		if (updateDto.getOwner() != null) {
			gameRoom.setOwner(updateDto.getOwner());
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

		return update(gameRoom);
	}

	@Override
	public GameRoom update(GameRoom gameRoom) {
		return gameRoomRepository.save(gameRoom);
	}

	@Override
	public void delete(Long id) throws JsonProcessingException {
		delete(getById(id));
	}

	@Override
	public void delete(GameRoom gameRoom) throws JsonProcessingException {
		deleteOperation(gameRoom);
	}

	private void deleteOperation(GameRoom gameRoom) throws JsonProcessingException {
		gameRoom.setEnabled(Enabled.FALSE);
		gameRoom.getPlayers().clear();
		gameRoom.getPlayerRankMap().clear();
		gameRoom.setDeleted(new Date());
	}
}
