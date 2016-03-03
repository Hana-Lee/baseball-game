package kr.co.leehana.service;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-03-02 19:55
 */
public interface ChatService {

	Chat create(Chat chat);

	Chat getById(Long id);

	Chat getByIdAndEnabled(Long id, Enabled enabled);

	List<Chat> getAll();

	List<Chat> getAllByEnabled(Enabled enabled);

	Page<Chat> getAll(Pageable pageable);

	Page<Chat> getAllByEnabled(Enabled enabled, Pageable pageable);

	void delete(Long id);

	void delete(Chat chat);
}
