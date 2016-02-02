package kr.co.leehana.service.impl;

import kr.co.leehana.dto.GameRoomDto;
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
	public GameRoom create(GameRoomDto.Create createDto) {
		GameRoom gameRoom = modelMapper.map(createDto, GameRoom.class);

		if (gameRoomRepository.findByOwner(createDto.getOwner()) != null) {
			log.error("owner duplicated exception. {} : {}", createDto.getOwner().getId(), createDto.getOwner()
					.getNickname());
			throw new OwnerDuplicatedException(createDto.getOwner());
		}

		fillInitData(gameRoom);

		return gameRoomRepository.save(gameRoom);
	}

	private void fillInitData(GameRoom gameRoom) {
		gameRoom.setNumber(0);

		gameRoom.getPlayers().add(gameRoom.getOwner());
		gameRoom.getPlayerRankMap().put(1, gameRoom.getOwner());

		Date now = new Date();
		gameRoom.setCreated(now);
		gameRoom.setUpdated(now);
	}

	@Override
	public GameRoom get(Long id) {
		return null;
	}

	@Override
	public List<GameRoom> getAll() {
		return null;
	}

	@Override
	public Page<GameRoom> getAll(Pageable pageable) {
		return gameRoomRepository.findAll(pageable);
	}

	@Override
	public GameRoom update(GameRoomDto.Update updateDto) {
		return null;
	}

	@Override
	public void delete(Long id) {

	}
}