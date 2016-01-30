package kr.co.leehana.service.impl;

import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.exception.AccountNotFoundException;
import kr.co.leehana.exception.UserDuplicatedException;
import kr.co.leehana.model.Account;
import kr.co.leehana.model.Level;
import kr.co.leehana.model.Lose;
import kr.co.leehana.model.MatchRecord;
import kr.co.leehana.model.TotalGame;
import kr.co.leehana.model.TotalRank;
import kr.co.leehana.model.Win;
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

	private final AccountRepository accountRepository;

	private final ModelMapper modelMapper;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper, PasswordEncoder
			passwordEncoder) {
		this.accountRepository = accountRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Account create(AccountDto.Create dto) {
		Account account = modelMapper.map(dto, Account.class);
		String email = dto.getEmail();
		if (accountRepository.findByEmail(email) != null) {
			log.error("user duplicated exception. {}", email);
			throw new UserDuplicatedException(email);
		}

		fillInitData(account);

		Account newAccount = accountRepository.save(account);
		return newAccount;
	}

	private void fillInitData(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));

		account.setAdmin(false);
		account.setLevel(new Level(1));

		final MatchRecord matchRecord = new MatchRecord();
		matchRecord.setLose(new Lose(0));
		matchRecord.setWin(new Win(0));
		matchRecord.setTotalGame(new TotalGame(0));

		account.setMatchRecord(matchRecord);
		account.setTotalRank(new TotalRank(0));

		final Date now = new Date();
		account.setJoined(now);
		account.setUpdated(now);
	}

	@Override
	public Account update(long id, AccountDto.Update updateDto) {
		final Account account = get(id);
		account.setEmail(updateDto.getEmail());
		account.setNickname(updateDto.getNickname());
		account.setPassword(passwordEncoder.encode(updateDto.getPassword()));
		account.setUpdated(new Date());

		return accountRepository.save(account);
	}

	@Override
	public Account updateStatus(long id, AccountDto.UpdateStatus updateStatusDto) {
		final Account account = get(id);
		account.setLevel(updateStatusDto.getLevel());
		account.setMatchRecord(updateStatusDto.getMatchRecord());
		account.setTotalRank(updateStatusDto.getTotalRank());
		account.setUpdated(new Date());

		return accountRepository.save(account);
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
		accountRepository.delete(get(id));
	}
}
