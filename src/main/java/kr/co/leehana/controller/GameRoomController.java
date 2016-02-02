package kr.co.leehana.controller;

import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.ErrorResponse;
import kr.co.leehana.exception.OwnerDuplicatedException;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
 * @since 2016-01-31 21:05
 */
@RestController
public class GameRoomController {

	private static final String URL_VALUE = "/game_room";
	private static final String URL_WITH_ID_VALUE = URL_VALUE + "/{id}";

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
		    "owner": {
		      "id": 1,
		      "email": "i@leehana.co.kr",
		      "nickname": "이하나",
		      "gameRole": "ATTACKER"
		    },
		    "setting": {
		        "limitWrongInputCount": 5,
		        "limitGuessInputCount": 10,
		        "generationNumberCount": 3
		    }
		}
	 */
	@RequestMapping(value = {URL_VALUE}, method = {RequestMethod.POST})
	public ResponseEntity create(@RequestBody @Valid GameRoomDto.Create createDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage(bindingResult.getFieldError().getDefaultMessage());
			errorResponse.setErrorCode("gameRoom.bad.request");

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		Player updatedPlayer = updatePlayerStatus(createDto);
		createDto.setOwner(updatedPlayer);

		GameRoom newGameRoom = gameRoomService.create(createDto);

		return new ResponseEntity<>(newGameRoom, HttpStatus.CREATED);
	}

	private Player updatePlayerStatus(GameRoomDto.Create createDto) {
		PlayerDto.Update playerUpdateDto = modelMapper.map(createDto.getOwner(), PlayerDto.Update.class);
		return playerService.update(createDto.getOwner().getId(), playerUpdateDto);
	}

	@RequestMapping(value = {URL_VALUE}, method = {RequestMethod.GET})
	@ResponseStatus(code = HttpStatus.OK)
	public PageImpl<GameRoomDto.Response> getGameRooms(Pageable pageable) {
		Page<GameRoom> gameRooms = gameRoomService.getAll(pageable);

		List<GameRoomDto.Response> content = gameRooms.getContent().parallelStream().map(gameRoom -> modelMapper.map
				(gameRoom, GameRoomDto.Response.class)).collect(Collectors.toList());

		return new PageImpl<>(content, pageable, gameRooms.getTotalElements());
	}

	@ExceptionHandler(OwnerDuplicatedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleOwnerDuplicatedException(PlayerDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + ex.getEmail() + "] 중복된 Owner 입니다.");
		errorResponse.setErrorCode("duplicated.owner.exception");
		return errorResponse;
	}
}
