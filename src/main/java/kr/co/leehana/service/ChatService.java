package kr.co.leehana.service;

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

	List<Chat> getAll();

	Page<Chat> getAll(Pageable pageable);

	void delete(Long id);

	void delete(Chat chat);
}
