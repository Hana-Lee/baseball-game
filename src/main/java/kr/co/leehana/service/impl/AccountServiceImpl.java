package kr.co.leehana.service.impl;

import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.exception.AccountNotFoundException;
import kr.co.leehana.exception.UserDuplicatedException;
import kr.co.leehana.model.Account;
import kr.co.leehana.repository.AccountRepository;
import kr.co.leehana.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:12
 */
@Service
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Account create(AccountDto.Create dto) {
		Account account = modelMapper.map(dto, Account.class);
		String email = dto.getEmail();
		if (accountRepository.findByEmail(email) != null) {
			log.error("user duplicated exception. {}", email);
			throw new UserDuplicatedException(email);
		}

		fillInitData(account);

		return accountRepository.save(account);
	}

	private void fillInitData(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));

		account.setAdmin(false);
		// TODO: Level, MatchRecord, TotalRank 저장소 만들기
//		account.setLevel(new Level());
//		account.setMatchRecord(new MatchRecord());
//		account.setTotalRank(new Rank());

		final Date now = new Date();
		account.setJoined(now);
		account.setUpdated(now);
	}

	@Override
	public Account update(long id, AccountDto.Update updateDto) {
		return null;
	}

	@Override
	public Account updateStatus(long id, AccountDto.UpdateStatus updateDto) {
		return null;
	}

	@Override
	public Account get(long id) {
		Account account = accountRepository.findOne(id);
		if (account == null) {
			throw new AccountNotFoundException(id);
		}

		return account;
	}

	@Override
	public void delete(long id) {

	}
}
