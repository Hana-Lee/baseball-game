package kr.co.leehana.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.enums.GameRole;
import kr.co.leehana.enums.Status;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.GameRoleDuplicatedException;
import kr.co.leehana.exception.GameRoomNotFoundException;
import kr.co.leehana.exception.GameRoomPlayerNotFoundException;
import kr.co.leehana.exception.GameRoomPlayersNotEmpty;
import kr.co.leehana.exception.GameRoomRunningException;
import kr.co.leehana.exception.OwnerChangeException;
import kr.co.leehana.exception.OwnerDuplicatedException;
import kr.co.leehana.model.AttackerRoleCount;
import kr.co.leehana.model.DefenderRoleCount;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.RandomNumber;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
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
import java.util.Objects;
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
 * @since 2016-01-31 21:05
 */
@RestController
public class GameRoomController {

	public static final String URL_VALUE = "/gameroom";
	private static final String URL_ALL_VALUE = URL_VALUE + "/all";
	private static final String URL_WITH_ID_VALUE = URL_VALUE + "/{id}";
	private static final String URL_JOIN_VALUE = URL_VALUE + "/join/{id}";
	private static final String URL_CHANGE_OWNER_VALUE = URL_VALUE + "/change-owner/{id}";
	private static final String URL_LEAVE_VALUE = URL_VALUE + "/leave/{id}";
	private static final String URL_READY_VALUE = URL_VALUE + "/ready/{id}";

	private final GameRoomService gameRoomService;
	private final PlayerService playerService;
	private final ModelMapper modelMapper;
	private final GenerationNumberStrategy generationNumberStrategy;

	@Autowired
	public GameRoomController(GameRoomService gameRoomService, PlayerService playerService, ModelMapper modelMapper,
	                          GenerationNumberStrategy generationNumberStrategy) {
		this.gameRoomService = gameRoomService;
		this.playerService = playerService;
		this.modelMapper = modelMapper;
		this.generationNumberStrategy = generationNumberStrategy;
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
	@NotifyClients(
			url = {"/topic/gameroom/list/updated", "/topic/player/list/updated"},
			operation = {"insert", "delete"})
	@RequestMapping(value = {URL_VALUE}, method = {POST})
	public ResponseEntity create(@RequestBody @Valid GameRoomDto.Create createDto, BindingResult bindingResult) throws
			JsonProcessingException {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		ownerSetting(createDto);

		createDto.setRoomNumber(generationNumberStrategy.generateRoomNumber());

		GameRoom newGameRoom = gameRoomService.create(createDto);

		return new ResponseEntity<>(newGameRoom, CREATED);
	}

	private void ownerSetting(GameRoomDto.Create createDto) {
		UserDetailsImpl owner = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		PlayerDto.Update playerUpdateDto = new PlayerDto.Update();
		playerUpdateDto.setGameRole(createDto.getGameRole());
		playerUpdateDto.setStatus(Status.READY_BEFORE);
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

	@NotifyClients(
			url = {"/topic/gameroom/{id}/updated", "/topic/gameroom/list/updated"},
			operation = {"update", "update"})
	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {PUT})
	public ResponseEntity update(@PathVariable Long id, @RequestBody GameRoomDto.Update updateDto) {
		if (updateDtoHasAllFieldNullValue(updateDto)) {
			return createErrorResponseEntity("Update fields are must not be null", null);
		}

		GameRoom updatedGameRoom = gameRoomService.update(id, updateDto);
		return new ResponseEntity<>(updatedGameRoom, OK);
	}

	private boolean updateDtoHasAllFieldNullValue(GameRoomDto.Update updateDto) {
		return updateDto == null || (updateDto.getGameCount() == null && updateDto.getName() == null && updateDto
				.getPlayerRankMap() == null && updateDto.getPlayers() == null && updateDto.getSetting() == null);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {DELETE})
	public ResponseEntity delete(@PathVariable Long id) throws JsonProcessingException {
		gameRoomService.delete(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@NotifyClients(
			url = {"/topic/gameroom/list/updated", "/topic/gameroom/{id}/updated", "/topic/player/list/updated"},
			operation = {"update", "join", "delete"})
	@RequestMapping(value = {URL_JOIN_VALUE}, method = {PATCH})
	public ResponseEntity join(@PathVariable Long id, @RequestBody @Valid GameRoomDto.Join joinDto, BindingResult
			bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		GameRoom gameRoom = gameRoomService.getById(id);

		if (Objects.equals(gameRoom.getStatus(), Status.RUNNING)) {
//			throw new GameRoomRunningException("Do not join already running game room");
		}

		if (Objects.equals(joinDto.getGameRole(), GameRole.DEFENDER)) {
			Long defenderCount = gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getGameRole(), GameRole
					.DEFENDER)).count();
			if (defenderCount > 0) {
				throw new GameRoleDuplicatedException(GameRole.DEFENDER);
			}
		}

		UserDetailsImpl joinPlayerImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Player joinPlayer = playerService.getByEmail(joinPlayerImpl.getEmail());
		joinPlayer.setGameRole(joinDto.getGameRole());
		joinPlayer.setStatus(Status.READY_BEFORE);

		if (Objects.equals(joinPlayer.getGameRole(), GameRole.DEFENDER)) {
			joinPlayer.setDefenderRoleCount(new DefenderRoleCount(joinPlayer.getDefenderRoleCount().getValue() + 1));
		} else if (Objects.equals(joinPlayer.getGameRole(), GameRole.ATTACKER)) {
			joinPlayer.setAttackerRoleCount(new AttackerRoleCount(joinPlayer.getAttackerRoleCount().getValue() + 1));
		}

		gameRoom.getPlayers().add(joinPlayer);

		gameRoomService.update(id, modelMapper.map(gameRoom, GameRoomDto.Update.class));

		return new ResponseEntity<>(gameRoom, OK);
	}

	@NotifyClients(
			url = {"/topic/gameroom/list/updated", "/topic/gameroom/{id}/updated"},
			operation = {"update", "changeOwner"})
	@RequestMapping(value = {URL_CHANGE_OWNER_VALUE}, method = {PATCH})
	public ResponseEntity changeOwner(@PathVariable Long id, @RequestBody @Valid GameRoomDto.ChangeOwner
			changeOwnerDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}

		if (Objects.equals(changeOwnerDto.getNewOwnerId(), changeOwnerDto.getOldOwnerId())) {
			throw new OwnerChangeException("New owner ID and old owner ID is not allow same ID [ " + changeOwnerDto
					.getNewOwnerId() + ":" + changeOwnerDto.getOldOwnerId() + " ]");
		}

		GameRoom gameRoom = gameRoomService.getById(id);

		checkCurrentPlayerIsOwner(gameRoom);

		Player newOwner = playerService.getById(changeOwnerDto.getNewOwnerId());
		gameRoom.setOwner(newOwner);

		gameRoomService.update(gameRoom);

		return new ResponseEntity<>(gameRoom, OK);
	}

	@NotifyClients(
			url = {"/topic/gameroom/list/updated", "/topic/player/list/updated"},
			operation = {"delete", "insert"})
	@RequestMapping(value = {URL_LEAVE_VALUE}, method = {DELETE})
	public ResponseEntity leaveAndDelete(@PathVariable Long id) throws JsonProcessingException {
		GameRoom gameRoom = gameRoomService.getById(id);

		if (Objects.equals(gameRoom.getStatus(), Status.RUNNING)) {
//			throw new GameRoomRunningException("Game room still running");
		}

		Player player = getCurrentPlayer();

		if (!gameRoom.getPlayers().contains(player)) {
			throw new GameRoomPlayerNotFoundException(gameRoom, player);
		}

		player.setGameRole(null);
		player.setStatus(null);

		gameRoom.getPlayers().remove(player);

		if (gameRoom.getPlayers().isEmpty()) {
			gameRoomService.delete(gameRoom);

			return new ResponseEntity<>(gameRoom, NO_CONTENT);
		} else {
			throw new GameRoomPlayersNotEmpty("Game Room No. " + gameRoom.getId() + " player list must be empty. " +
					"[player count : " + gameRoom.getPlayers().size() + "]");
		}
	}

	@NotifyClients(
			url = {"/topic/gameroom/list/updated", "/topic/gameroom/{id}/updated", "/topic/player/list/updated"},
			operation = {"update", "leave", "insert"})
	@RequestMapping(value = {URL_LEAVE_VALUE}, method = {PATCH})
	public ResponseEntity leave(@PathVariable Long id) throws JsonProcessingException {
		GameRoom gameRoom = gameRoomService.getById(id);

		if (Objects.equals(gameRoom.getStatus(), Status.RUNNING)) {
//			throw new GameRoomRunningException("Game room still running");
		}

		Player player = getCurrentPlayer();

		if (!gameRoom.getPlayers().contains(player)) {
			throw new GameRoomPlayerNotFoundException(gameRoom, player);
		}

		player.setGameRole(null);
		player.setStatus(null);

		gameRoom.getPlayers().remove(player);

		Integer playerRankKey = null;
		for (Integer key : gameRoom.getPlayerRankMap().keySet()) {
			if (Objects.equals(gameRoom.getPlayerRankMap().get(key), player)) {
				playerRankKey = key;
			}
		}

		if (playerRankKey != null) {
			gameRoom.getPlayerRankMap().remove(playerRankKey);
		}

		if (Objects.equals(gameRoom.getOwner(), player)) {
			gameRoom.setOwner(gameRoom.getPlayers().iterator().next());
		}

		gameRoomService.update(gameRoom);

		return new ResponseEntity<>(gameRoom, OK);
	}

	@NotifyClients(
			url = {"/topic/gameroom/{id}/updated", "/topic/gameroom/list/updated"},
			operation = {"ready", "update"})
	@RequestMapping(value = {URL_READY_VALUE}, method = {PATCH})
	public ResponseEntity readyAndSetNumber(@PathVariable Long id, @RequestBody @Valid GameRoomDto.Ready readyDto,
	                                        BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createErrorResponseEntity(bindingResult);
		}
		GameRoom gameRoom = gameRoomService.getById(id);

		if (Objects.equals(gameRoom.getStatus(), Status.RUNNING)) {
			throw new GameRoomRunningException("Game room is already running");
		}

		Player readyPlayer = getCurrentPlayer();
		gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getEmail(), readyPlayer.getEmail())).findFirst()
				.get().setStatus(readyDto.getStatus());

		if (isAllPlayersReadyDone(gameRoom)) {
			makeRandomNumber(gameRoom, readyDto, readyPlayer);
			gameRoom.setGameCount(gameRoom.getGameCount() + 1);
			gameRoom.setStatus(Status.RUNNING);
		} else {
			gameRoom.setStatus(Status.NORMAL);
		}

		gameRoomService.update(gameRoom);
		return new ResponseEntity<>(gameRoom, OK);
	}

	private boolean isAllPlayersReadyDone(GameRoom gameRoom) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(Status.READY_DONE, p.getStatus())).count() ==
				gameRoom.getPlayers().size();
	}

	private void makeRandomNumber(GameRoom gameRoom, GameRoomDto.Ready readyDto, Player currentPlayer) {
		if (isDefenderPlayer(gameRoom, currentPlayer) && StringUtils.isNotBlank(readyDto.getNumber())) {
			gameRoom.setRandomNumber(new RandomNumber(readyDto.getNumber()));
		} else {
			gameRoom.setRandomNumber(new RandomNumber(generationNumberStrategy.generateRandomNumber(gameRoom.getSetting())));
		}
	}

	private boolean isDefenderPlayer(GameRoom gameRoom, Player currentPlayer) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(GameRole.DEFENDER, p.getGameRole()) &&
				Objects.equals(p.getEmail(), currentPlayer.getEmail())).count() > 0;
	}

	private Player getCurrentPlayer() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		return playerService.getByEmail(userDetails.getEmail());
	}

	private void checkCurrentPlayerIsOwner(GameRoom gameRoom) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Player currentPlayer = playerService.getByEmail(userDetails.getEmail());

		if (!Objects.equals(gameRoom.getOwner().getEmail(), currentPlayer.getEmail())) {
			throw new OwnerChangeException(currentPlayer.getNickname() + " is not game room owner.");
		}
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
			errorCode = "gameroom.bad.request";
		}

		return new ResponseEntity<>(createErrorResponse(message, errorCode), BAD_REQUEST);
	}

	@ExceptionHandler(GameRoleDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoleDuplicatedException(GameRoleDuplicatedException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(OwnerDuplicatedException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleOwnerDuplicatedException(OwnerDuplicatedException ex) {
		return createErrorResponse("[" + ex.getOwner().getEmail() + "] 중복된 방장 입니다.", "duplicated.owner.exception");
	}

	@ExceptionHandler(GameRoomNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomNotFoundException(GameRoomNotFoundException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(OwnerChangeException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleOwnerChangeException(OwnerChangeException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomPlayerNotFoundException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomPlayerNotFoundException(GameRoomPlayerNotFoundException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomPlayersNotEmpty.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorResponse handleGameRoomPlayersNotEmptyException(GameRoomPlayersNotEmpty ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	@ExceptionHandler(GameRoomRunningException.class)
	public ErrorResponse handleGameRoomAlreadyRunningException(GameRoomRunningException ex) {
		return createErrorResponse(ex.getMessage(), ex.getErrorCode());
	}

	private ErrorResponse createErrorResponse(String message, String errorCode) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage(message);
		errorResponse.setErrorCode(errorCode);
		return errorResponse;
	}
}
