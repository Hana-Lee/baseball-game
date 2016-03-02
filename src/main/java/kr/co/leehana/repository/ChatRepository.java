package kr.co.leehana.repository;

import kr.co.leehana.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Hana Lee
 * @since 2016-03-02 19:54
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
