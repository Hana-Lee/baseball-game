package kr.co.leehana.service;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.enums.Enabled;
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

	Player updateById(Long id, PlayerDto.Update updateDto);

	Player updateByEmail(String email, PlayerDto.Update updateDto);

	Player getById(Long id);

	Player getByIdAndEnabled(Long id, Enabled enabled);

	Player getByEmail(String email);

	Player getByEmailAndEnabled(String email, Enabled enabled);

	Page<Player> getAll(Pageable pageable);

	Page<Player> getAllByEnabled(Enabled enabled, Pageable pageable);

	List<Player> getAll();

	List<Player> getAllByEnabled(Enabled enabled);

	void delete(Long id);

	void delete(Player player);
}
