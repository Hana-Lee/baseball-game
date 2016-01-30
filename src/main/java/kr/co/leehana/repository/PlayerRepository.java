package kr.co.leehana.repository;

import kr.co.leehana.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:25
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

	Player findByEmail(String email);
}
