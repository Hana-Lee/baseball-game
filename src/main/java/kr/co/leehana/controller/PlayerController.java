package kr.co.leehana.controller;

import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.exception.PlayerNotLoggedInException;
import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.PlayerService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
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
	private static final String URL_LOGGED_IN_USERS_VALUE = URL_VALUE + "/login/true";
	private static final String URL_READY_VALUE = URL_VALUE + "/ready";

	private PlayerService playerService;
	private ModelMapper modelMapper;
	private SessionRegistry sessionRegistry;

	@Autowired
	public PlayerController(PlayerService playerService, ModelMapper modelMapper, SessionRegistry sessionRegistry) {
		this.playerService = playerService;
		this.modelMapper = modelMapper;
		this.sessionRegistry = sessionRegistry;
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

	@RequestMapping(value = {URL_LOGGED_IN_USERS_VALUE}, method = {GET})
	public ResponseEntity getLoggedInPlayers() {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<PlayerDto.Response> loggedInPlayers = new ArrayList<>();
		String currentPlayerEmail = getCurrentPlayerEmail();
		principals.stream().filter(principal -> principal instanceof UserDetailsImpl).forEach(principal -> {
			String email = ((UserDetailsImpl) principal).getEmail();
			if (!currentPlayerEmail.equals(email)) {
				Player player = playerService.getByEmailAndEnabledAndNoJoinedRoom(email);
				if (player != null) {
					PlayerDto.Response responseDto = modelMapper.map(player, PlayerDto.Response.class);
					loggedInPlayers.add(responseDto);
				}
			}
		});

		return new ResponseEntity<>(loggedInPlayers, OK);
	}

	private String getCurrentPlayerEmail() {
		return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
	}

	@RequestMapping(value = {URL_VALUE}, method = {GET})
	public ResponseEntity getLoggedInPlayer() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal == null) {
			throw new PlayerNotLoggedInException("Player do not logged in");
		}

		Player player = playerService.getByEmail(((UserDetailsImpl) principal).getEmail());
		return new ResponseEntity<>(modelMapper.map(player, PlayerDto.Response.class), OK);
	}

	@NotifyClients(url = {"/topic/player/updated"}, operation = {"ready"})
	@RequestMapping(value = {URL_READY_VALUE}, method = {PATCH})
	public ResponseEntity readyOrReadyCancel(@RequestBody @Valid PlayerDto.Ready readyDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}
		final Player player = getCurrentPlayer();
		player.setStatus(readyDto.getStatus());
		playerService.update(player);
		return new ResponseEntity<>(modelMapper.map(player, PlayerDto.Response.class), OK);
	}

	private Player getCurrentPlayer() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		return playerService.getByEmail(userDetails.getEmail());
	}

	@ExceptionHandler(PlayerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerDuplicatedException(PlayerDuplicatedException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	@ExceptionHandler(PlayerNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerNotFoundException(PlayerNotFoundException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	@ExceptionHandler(PlayerNotLoggedInException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handlePlayerNotLoggedInException(PlayerNotLoggedInException e) {
		return createErrorResponse(e.getMessage(), e.getErrorCode());
	}

	private ErrorResponse createErrorResponse(String message, String errorCode) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(message);
		errorResponse.setErrorCode(errorCode);
		return errorResponse;
	}

	private ResponseEntity createErrorResponseEntity(BindingResult bindingResult) {
		String message;
		if (bindingResult.getFieldError() != null) {
			message = bindingResult.getFieldError().getDefaultMessage();
		} else if (bindingResult.getGlobalError() != null) {
			message = bindingResult.getGlobalError().getDefaultMessage();
		} else {
			message = "Binding error";
		}
		return createErrorResponseEntity(message, null);
	}

	private ResponseEntity createErrorResponseEntity(String message, String errorCode) {
		if (StringUtils.isBlank(errorCode)) {
			errorCode = "player.bad.request";
		}

		return new ResponseEntity<>(createErrorResponse(message, errorCode), BAD_REQUEST);
	}
}
