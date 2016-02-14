package kr.co.leehana.controller;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.model.Player;
import kr.co.leehana.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hana Lee
 * @since 2016-01-14 22-39
 */
@RestController
public class PlayerController {

	public static final String URL_VALUE = "/player";
	private static final String URL_ALL_VALUE = URL_VALUE + "/all";
	private static final String URL_WITH_ID_VALUE = URL_VALUE + "/{id}";

	private PlayerService playerService;

	private ModelMapper modelMapper;

	@Autowired
	public PlayerController(PlayerService playerService, ModelMapper modelMapper) {
		this.playerService = playerService;
		this.modelMapper = modelMapper;
	}

	@RequestMapping(value = {URL_VALUE}, method = {POST})
	public ResponseEntity create(@RequestBody @Valid PlayerDto.Create createDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			String message;
			if (bindingResult.getFieldError() != null) {
				message = bindingResult.getFieldError().getDefaultMessage();
			} else if (bindingResult.getGlobalError() != null) {
				message = bindingResult.getGlobalError().getDefaultMessage();
			} else {
				message = "DTO Object binding error";
			}
			errorResponse.setMessage(message);
			errorResponse.setErrorCode("bad.request");
			return new ResponseEntity<>(errorResponse, BAD_REQUEST);
		}

		Player newPlayer = playerService.create(createDto);
		return new ResponseEntity<>(newPlayer, CREATED);
	}

	@RequestMapping(value = {URL_ALL_VALUE}, method = {GET})
	@ResponseStatus(code = OK)
	public PageImpl<PlayerDto.Response> getPlayers(Pageable pageable) {
		Page<Player> pages = playerService.getAll(pageable);
		List<PlayerDto.Response> content = pages.getContent().parallelStream().map(player -> modelMapper.map(player,
				PlayerDto.Response.class)).collect(Collectors.toList());
		return new PageImpl<>(content, pageable, pages.getTotalElements());
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {GET})
	@ResponseStatus(code = OK)
	public PlayerDto.Response getPlayer(@PathVariable Long id) {
		return modelMapper.map(playerService.getById(id), PlayerDto.Response.class);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {PUT})
	public ResponseEntity update(@PathVariable Long id, @RequestBody @Valid PlayerDto.Update updateDto, BindingResult
			bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(BAD_REQUEST);
		}

		Player updatedPlayer = playerService.updateById(id, updateDto);
		return new ResponseEntity<>(modelMapper.map(updatedPlayer, PlayerDto.Response.class), OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {DELETE})
	public ResponseEntity delete(@PathVariable Long id) {
		playerService.delete(id);

		return new ResponseEntity<>(NO_CONTENT);
	}

	@ExceptionHandler(PlayerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerDuplicatedException(PlayerDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + ex.getEmail() + "] 중복된 e-mail 입니다.");
		errorResponse.setErrorCode("duplicated.email.exception");
		return errorResponse;
	}

	@ExceptionHandler(PlayerNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerNotFoundException(PlayerNotFoundException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(e.getMessage());
		errorResponse.setErrorCode(e.getErrorCode());
		return errorResponse;
	}
}
