package kr.co.leehana.controller;

import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.UserDuplicatedException;
import kr.co.leehana.model.Account;
import kr.co.leehana.repository.AccountRepository;
import kr.co.leehana.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hana Lee
 * @since 2016-01-14 22-39
 */
@RestController
public class AccountController {

	private static final String URL_VALUE = "/accounts";
	private static final String URL_WITH_ID_VALUE = URL_VALUE + "/{id}";
	private static final String STATUS_URL_WITH_ID_VALUE = URL_VALUE + "/status/{id}";

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ModelMapper modelMapper;

	@RequestMapping(value = {URL_VALUE}, method = {RequestMethod.POST})
	public ResponseEntity create(@RequestBody @Valid AccountDto.Create createDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage(bindingResult.getFieldError().getDefaultMessage());
			errorResponse.setErrorCode("bad.request");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		Account newAccount = accountService.create(createDto);
		return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
	}

	@RequestMapping(value = {URL_VALUE}, method = {RequestMethod.GET})
	@ResponseStatus(code = HttpStatus.OK)
	public PageImpl<AccountDto.Response> getAccounts(Pageable pageable) {
		Page<Account> pages = accountRepository.findAll(pageable);
		List<AccountDto.Response> content = pages.getContent().parallelStream().map(account -> modelMapper.map
				(account, AccountDto.Response.class)).collect(Collectors.toList());
		return new PageImpl<>(content, pageable, pages.getTotalElements());
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {RequestMethod.PUT})
	public ResponseEntity update(@PathVariable long id, @RequestBody @Valid AccountDto.Update updateDto, BindingResult
			bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Account updatedAccount = accountService.update(id, updateDto);
		return new ResponseEntity<>(modelMapper.map(updatedAccount, AccountDto.Response.class), HttpStatus.OK);
	}

	@RequestMapping(value = {STATUS_URL_WITH_ID_VALUE}, method = {RequestMethod.PUT})
	public ResponseEntity updateStatus(@PathVariable long id, @RequestBody @Valid AccountDto.UpdateStatus
			updateStatusDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Account updatedAccount = accountService.updateStatus(id, updateStatusDto);
		return new ResponseEntity<>(modelMapper.map(updatedAccount, AccountDto.Response.class), HttpStatus.OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {RequestMethod.DELETE})
	public ResponseEntity delete(@PathVariable long id) {
		accountService.delete(id);

		return new ResponseEntity(HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(UserDuplicatedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUserDuplicatedException(UserDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + ex.getEmail() + "] 중복된 e-mail 입니다.");
		errorResponse.setErrorCode("duplicated.email.exception");
		return errorResponse;
	}
}
