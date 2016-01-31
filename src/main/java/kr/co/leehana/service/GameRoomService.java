package kr.co.leehana.service;

import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.model.GameRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:33
 */
public interface GameRoomService {

	GameRoom create(GameRoomDto.Create createDto);

	GameRoom get(Long id);

	List<GameRoom> getAll();

	Page<GameRoom> getAll(Pageable pageable);

	GameRoom update(GameRoomDto.Update updateDto);

	void delete(Long id);
}
