package kr.co.leehana.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.GameRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:33
 */
public interface GameRoomService {

	GameRoom create(GameRoomDto.Create createDto) throws JsonProcessingException;

	GameRoom getById(Long id);

	GameRoom getByIdAndEnabled(Long id, Enabled enabled);

	List<GameRoom> getAll();

	List<GameRoom> getAllByEnabled(Enabled enabled);

	Page<GameRoom> getAll(Pageable pageable);

	Page<GameRoom> getAllByEnabled(Enabled enabled, Pageable pageable);

	GameRoom update(Long id, GameRoomDto.Update updateDto);

	void delete(Long id);

	void delete(GameRoom gameRoom) throws JsonProcessingException;
}
