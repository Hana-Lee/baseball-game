package kr.co.leehana.repository;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:48
 */
@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

	GameRoom findOneByOwnerAndEnabled(Player player, Enabled enabled);

	GameRoom findOneByIdAndEnabled(Long id, Enabled enabled);

	List<GameRoom> findAllByEnabled(Enabled enabled);

	Page<GameRoom> findAllByEnabled(Enabled enabled, Pageable pageable);
}
