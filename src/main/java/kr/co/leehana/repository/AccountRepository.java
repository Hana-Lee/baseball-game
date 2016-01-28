package kr.co.leehana.repository;

import kr.co.leehana.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:25
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Account findByEmail(String email);
}
