package kr.co.leehana.repository;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:25
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

	Player findOneByIdAndEnabled(Long id, Enabled enabled);

	Player findOneByEmail(String email);

	Player findOneByEmailAndEnabled(String email, Enabled enabled);

	@Query(value = "select * from player where email = ?1 and enabled = 'TRUE' and joined_room_id is null", nativeQuery = true)
	Player findOneByEmailAndEnabledAndNoJoinedRoom(String email);

	List<Player> findAllByEnabled(Enabled enabled);

	Page<Player> findAllByEnabled(Enabled enabled, Pageable pageable);
}
