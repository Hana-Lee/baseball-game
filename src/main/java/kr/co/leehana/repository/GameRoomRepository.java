package kr.co.leehana.repository;

import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:48
 */
@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

	GameRoom findByOwner(Player player);
}
