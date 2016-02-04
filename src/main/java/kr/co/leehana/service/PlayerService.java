package kr.co.leehana.service;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:12
 */
public interface PlayerService {

	Player create(PlayerDto.Create createDto);

	Player update(Long id, PlayerDto.Update updateDto);

	Player getById(Long id);

	Player getByEmail(String email);

	Page<Player> getAll(Pageable pageable);

	List<Player> getAll();

	void delete(Long id);
}
