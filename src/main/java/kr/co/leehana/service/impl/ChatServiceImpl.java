package kr.co.leehana.service.impl;

import kr.co.leehana.enums.Enabled;
import kr.co.leehana.model.Chat;
import kr.co.leehana.repository.ChatRepository;
import kr.co.leehana.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2016-03-02 20:04
 */
@Service
@Transactional
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;

	@Autowired
	public ChatServiceImpl(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	@Override
	public Chat create(Chat chat) {
		fillInitData(chat);

		return chatRepository.save(chat);
	}

	private void fillInitData(Chat chat) {
		final Date now = new Date();
		chat.setCreated(now);

		chat.setEnabled(Enabled.TRUE);
	}

	@Override
	public Chat getById(Long id) {
		return getByIdAndEnabled(id, Enabled.TRUE);
	}

	@Override
	public Chat getByIdAndEnabled(Long id, Enabled enabled) {
		return chatRepository.findOneByIdAndEnabled(id, enabled);
	}

	@Override
	public List<Chat> getAll() {
		return getAllByEnabled(Enabled.TRUE);
	}

	@Override
	public List<Chat> getAllByEnabled(Enabled enabled) {
		return chatRepository.findAllByEnabled(enabled);
	}

	@Override
	public Page<Chat> getAll(Pageable pageable) {
		return getAllByEnabled(Enabled.TRUE, pageable);
	}

	@Override
	public Page<Chat> getAllByEnabled(Enabled enabled, Pageable pageable) {
		return chatRepository.findAllByEnabled(enabled, pageable);
	}

	@Override
	public void delete(Long id) {
		Chat chat = getById(id);
		chat.setEnabled(Enabled.FALSE);
		chat.setDeleted(new Date());
//		chatRepository.delete(id);
	}

	@Override
	public void delete(Chat chat) {
		chat.setEnabled(Enabled.FALSE);
		chat.setDeleted(new Date());
//		chatRepository.delete(chat);
	}
}
