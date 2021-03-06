package kr.co.leehana.controller;

import kr.co.leehana.annotation.NotifyClients;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.PlayerNotLoggedInException;
import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
@Slf4j
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
	public ResponseEntity<PlayerDto.Response> create(@RequestBody @Valid PlayerDto.Create createDto) {
		Player newPlayer = playerService.create(createDto);
		return new ResponseEntity<>(modelMapper.map(newPlayer, PlayerDto.Response.class), CREATED);
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
	public ResponseEntity<PlayerDto.Response> getPlayer(@PathVariable Long id) {
		return new ResponseEntity<>(modelMapper.map(playerService.getById(id), PlayerDto.Response.class), OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {PUT})
	public ResponseEntity<PlayerDto.Response> update(@PathVariable Long id, @RequestBody @Valid PlayerDto.Update updateDto) {
		Player updatedPlayer = playerService.updateById(id, updateDto);
		return new ResponseEntity<>(modelMapper.map(updatedPlayer, PlayerDto.Response.class), OK);
	}

	@RequestMapping(value = {URL_WITH_ID_VALUE}, method = {DELETE})
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		playerService.delete(id);

		return new ResponseEntity<>(NO_CONTENT);
	}

	@RequestMapping(value = {URL_LOGGED_IN_USERS_VALUE}, method = {GET})
	public ResponseEntity<List<PlayerDto.Response>> getLoggedInPlayers() {
		List<Object> principals = sessionRegistry.getAllPrincipals();
		List<PlayerDto.Response> loggedInPlayers = new ArrayList<>();
		String currentPlayerEmail = getCurrentPlayerEmail();
		principals.stream().filter(principal -> principal instanceof UserDetailsImpl).forEach(principal -> {
			String email = ((UserDetailsImpl) principal).getEmail();
			log.info("current player email : " + currentPlayerEmail);
			log.info("other player email : " + email);
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
	public ResponseEntity<PlayerDto.Response> getLoggedInPlayer() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal == null) {
			throw new PlayerNotLoggedInException("Player do not logged in");
		}

		Player player = playerService.getByEmail(((UserDetailsImpl) principal).getEmail());
		return new ResponseEntity<>(modelMapper.map(player, PlayerDto.Response.class), OK);
	}

	@NotifyClients(url = {"/topic/player/updated"}, operation = {"ready"})
	@RequestMapping(value = {URL_READY_VALUE}, method = {PATCH})
	public ResponseEntity<PlayerDto.Response> readyOrReadyCancel(@RequestBody @Valid PlayerDto.Ready readyDto) {
		final Player player = getCurrentPlayer();
		player.setStatus(readyDto.getStatus());

		// reset
		player.setInputCount(0);
		player.setWrongCount(0);
		player.setGuessNumber(null);
		player.getRank().setValue(0);
		player.getScore().setValue(0);
		player.getResult().getBall().setValue(0);
		player.getResult().getStrike().setValue(0);
		player.getResult().getSettlement().setSolved(false);

		playerService.update(player);
		return new ResponseEntity<>(modelMapper.map(player, PlayerDto.Response.class), OK);
	}

	private Player getCurrentPlayer() {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		return playerService.getByEmail(userDetails.getEmail());
	}
}
