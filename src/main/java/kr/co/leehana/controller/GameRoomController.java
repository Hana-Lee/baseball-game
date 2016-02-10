package kr.co.leehana.controller;

import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.GameRoleDuplicatedException;
import kr.co.leehana.exception.GameRoomNotFoundException;
import kr.co.leehana.exception.OwnerDuplicatedException;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import kr.co.leehana.type.GameRole;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Hana Lee
 * @since 2016-01-31 21:05
 */
@RestController
public class GameRoomController {

	public static final String URL_VALUE = "/gameroom";
	private static final String URL_ALL_VALUE = URL_VALUE + "/all";
	private static final String URL_WITH_ID_VALUE = URL_VALUE + "/{id}";
	private static final String URL_JOIN_VALUE = URL_VALUE + "/join/{id}";

	private final GameRoomService gameRoomService;
	private final PlayerService playerService;

	private ModelMapper modelMapper;

	@Autowired
	public GameRoomController(GameRoomService gameRoomService, ModelMapper modelMapper, PlayerService playerService) {
		this.gameRoomService = gameRoomService;
		this.modelMapper = modelMapper;
		this.playerService = playerService;
	}

	/*
		요청 셈플
		{
		    "name": "루비",
		    "gameRole": "ATTACKER",
		    "setting": {
		        "limitWrongInputCount": 5,
		        "limitGuessInputCount": 10,
		        "generationNumberCount": 3
		    }
		}
	 */
	@RequestMapping(value = {URL_VALUE}, method = {POST})
	public ResponseEntity create(@RequestBody @Valid GameRoomDto.Create createDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		ownerSetting(createDto);

		GameRoom newGameRoom = gameRoomService.create(createDto);

		return new ResponseEntity<>(newGameRoom, CREATED);
	}

	private void ownerSetting(GameRoomDto.Create createDto) {
		UserDetailsImpl owner = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		PlayerDto.Update playerUpdateDto = new PlayerDto.Update();
		playerUpdateDto.setGameRole(createDto.getGameRole());
		createDto.setOwner(playerService.updateByEmail(owner.getEmail(), playerUpdateDto));
	}

	@RequestMapping(value = {URL_ALL_VALUE}, method = {GET})
	@ResponseStatus(code = OK)
	public PageImpl<GameRoom> getGameRooms(Pageable pageable) {
		Page<GameRoom> gameRooms = gameRoomService.getAll(pageable);

		List<GameRoom> content = gameRooms.getContent().parallelStream().collect(Collectors.toList());

		return new PageImpl<>(content, pageable, gameRooms.getTotalElements());
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {GET})
	@ResponseStatus(OK)
	public GameRoom getGameRoom(@PathVariable Long id) {
		return gameRoomService.getById(id);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {PUT})
	public ResponseEntity update(@PathVariable Long id, @RequestBody @Valid GameRoomDto.Update updateDto,
	                             BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(BAD_REQUEST);
		}

		GameRoom updatedGameRoom = gameRoomService.update(id, updateDto);
		return new ResponseEntity<>(updatedGameRoom, OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {DELETE})
	public ResponseEntity delete(@PathVariable Long id) {
		gameRoomService.delete(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = {URL_JOIN_VALUE}, method = {POST})
	public ResponseEntity join(@PathVariable Long id, @RequestBody @Valid GameRoomDto.Join joinDto, BindingResult
			bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		GameRoom gameRoom = gameRoomService.getById(id);

		if (joinDto.getGameRole().equals(GameRole.DEFENDER)) {
			Long defenderCount = gameRoom.getPlayers().stream().filter(p -> p.getGameRole().equals(GameRole.DEFENDER))
					.count();
			if (defenderCount > 0) {
				throw new GameRoleDuplicatedException(GameRole.DEFENDER);
			}
		}

		UserDetailsImpl joinPlayerImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Player joinPlayer = playerService.getByEmail(joinPlayerImpl.getEmail());
		joinPlayer.setGameRole(joinDto.getGameRole());

		gameRoom.getPlayers().add(joinPlayer);

		return new ResponseEntity<>(gameRoom, OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {PATCH})
	public ResponseEntity changeOwner(@PathVariable Long id, @RequestBody @Valid GameRoomDto.ChangeOwner
			changeOwnerDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		if (changeOwnerDto.getNewOwnerId().equals(changeOwnerDto.getOldOwnerId())) {
			return createErrorResponseEntity(bindingResult);
		}

		GameRoom gameRoom = gameRoomService.getById(id);
		Player newOwner = playerService.getById(changeOwnerDto.getNewOwnerId());
		gameRoom.setOwner(newOwner);
		return new ResponseEntity<>(gameRoom, OK);
	}

	private ResponseEntity createErrorResponseEntity(BindingResult bindingResult) {
		return createErrorResponseEntity(bindingResult.getFieldError().getDefaultMessage(), null);
	}

	private ResponseEntity createErrorResponseEntity(String message, String errorCode) {
		if (StringUtils.isBlank(errorCode)) {
			errorCode = "gameRoom.bad.request";
		}

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(message);
		errorResponse.setErrorCode(errorCode);

		return new ResponseEntity<>(errorResponse, BAD_REQUEST);
	}

	@ExceptionHandler(GameRoleDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoleDuplicatedException(GameRoleDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setErrorCode(ex.getErrorCode());
		return errorResponse;
	}

	@ExceptionHandler(OwnerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleOwnerDuplicatedException(OwnerDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + ex.getOwner().getEmail() + "] 중복된 방장 입니다.");
		errorResponse.setErrorCode("duplicated.owner.exception");
		return errorResponse;
	}

	@ExceptionHandler(GameRoomNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomNotFoundException(PlayerNotFoundException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + e.getId() + "] 에 해당하는 게임룸이 없습니다.");
		errorResponse.setErrorCode("gameroom.not.found.exception");
		return errorResponse;
	}
}
