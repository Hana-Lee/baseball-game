package kr.co.leehana.service;

import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.model.Account;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:12
 */
public interface AccountService {

	Account create(AccountDto.Create dto);
	Account update(long id, AccountDto.Update updateDto);
	Account updateStatus(long id, AccountDto.UpdateStatus updateStatusDto);
	Account get(long id);
	void delete(long id);
}
