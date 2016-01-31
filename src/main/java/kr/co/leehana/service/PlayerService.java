package kr.co.leehana.service;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.Player;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:12
 */
public interface PlayerService {

	Player create(PlayerDto.Create createDto);

	Player update(Long id, PlayerDto.Update updateDto);

	Player get(Long id);

	void delete(Long id);
}
