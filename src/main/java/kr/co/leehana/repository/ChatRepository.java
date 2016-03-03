package kr.co.leehana.repository;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-03-02 19:54
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

	Chat findOneByIdAndEnabled(Long id, Enabled enabled);

	List<Chat> findAllByEnabled(Enabled enabled);

	Page<Chat> findAllByEnabled(Enabled enabled, Pageable pageable);
}