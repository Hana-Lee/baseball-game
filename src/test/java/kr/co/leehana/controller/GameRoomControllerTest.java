package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.enums.GameRole;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import kr.co.leehana.utils.TestPlayerCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static kr.co.leehana.enums.GameRole.ATTACKER;
import static kr.co.leehana.enums.GameRole.DEFENDER;
import static kr.co.leehana.utils.CommonsTestConstant.ERROR_CODE_PATH;
import static kr.co.leehana.utils.TestPlayerCreator.DEFAULT_TEST_PASS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Hana Lee
 * @since 2016-02-09 18:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {App.class})
@WebIntegrationTest(randomPort = false)
@Transactional
public class GameRoomControllerTest {

	private static final String BASE_URL = "/gameroom";
	private static final String CHANGE_OWNER_URL = BASE_URL + "/change-owner/%d";
	private static final String JOIN_URL = BASE_URL + "/join/%d";
	private static final String LEAVE_URL = BASE_URL + "/leave/%d";
	private static final String ALL_GAMEROOM_URL = BASE_URL + "/all";

	private static final String ROOM_NAME = "루비";

	private static final String GAMEROOM_BAD_REQUEST_CODE = "bad.request";
	private static final String GAMEROOM_ALL_FIELD_EMPTY_CODE = "gameroom.all.field.empty.exception";
	private static final String DUP_ERROR_CODE = "duplicated.owner.exception";
	private static final String DUP_GAME_ROLE_CODE = "duplicated.gameRole.exception";
	private static final String GAMEROOM_PLAYER_NOT_FOUND_CODE = "gameroom.player.not.found.exception";
	private static final String GAMEROOM_NOT_FOUND_CODE = "gameroom.not.found.exception";
	private static final String OWNER_CHANGE_EX_CODE = "owner.change.exception";

	private static final String SEC_EMAIL = "i2@leehana.co.kr";
	private static final String SEC_NICK = "이하나2";
	private static final String SEC_PASS = "dlgksk";
	private static final String THIRD_EMAIL = "i3@leehana.co.kr";
	private static final String THIRD_NICK = "이하나3";
	private static final String THIRD_PASSWORD = "dlgksk";

	private static final String OWNER_EMAIL_PATH = "$.owner.email";
	private static final String PLAYER_EMAIL_CONTAIN_PATH = "$.players[?(@.email == 'i@leehana.co.kr')]";
	private static final String SEC_PLAYER_EMAIL_CONTAIN_PATH = "$.players[?(@.email == '" + SEC_EMAIL + "')]";
	private static final String THIRD_PLAYER_EMAIL_CONTAIN_PATH = "$.players[?(@.email == 'i3@leehana.co.kr')]";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private GameRoomService gameRoomService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private TestPlayerCreator creator;

	private ObjectMapper objectMapper = new ObjectMapper();
	private MockMvc mockMvc;
	private Setting setting = new Setting();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain)
				.build();
	}

	@Test
	public void noLoginCreateGameRoomWithException() throws Exception {
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isUnauthorized());
	}

	@Test
	public void createGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.name", is(ROOM_NAME)));
		resultActions.andExpect(jsonPath(OWNER_EMAIL_PATH, is(player.getEmail())));
		resultActions.andExpect(jsonPath("$.owner.gameRole", is(ATTACKER.name())));
		resultActions.andExpect(jsonPath("$.players[0].email", is(player.getEmail())));
	}

	@Test
	public void createGameRoomWithDupOwnerException() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());

		resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content(objectMapper
				.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(DUP_ERROR_CODE)));
	}

	@Test
	public void createGameRoomWithDtoBindingError() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setSetting(setting);
		createDto.setGameRole(ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_BAD_REQUEST_CODE)));
	}

	@Test
	public void getGameRooms() throws Exception {
		Player player = creator.createTestPlayer();
		createTestGameRoom(player);

		ResultActions resultActions = mockMvc.perform(get(ALL_GAMEROOM_URL).with(httpBasic(player.getEmail(),
				DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.content", hasSize(1)));
	}

	@Test
	public void getGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom gameRoom = createTestGameRoom(player);

		ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/" + gameRoom.getId()).with(httpBasic(player
				.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.name", is("루비")));
	}

	@Test
	public void updateGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom gameRoom = createTestGameRoom(player);
		GameRoomDto.Update updateDto = new GameRoomDto.Update();
		updateDto.setName("골드");

		ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/" + gameRoom.getId()).contentType
				(APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)).with(httpBasic(player.getEmail
				(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.name", is("골드")));
	}

	@Test
	public void updateGameRoomWithDtoBindingError() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom gameRoom = createTestGameRoom(player);
		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(DEFENDER);

		ResultActions resultActions = mockMvc.perform(put(BASE_URL + "/" + gameRoom.getId()).contentType
				(APPLICATION_JSON).content(objectMapper.writeValueAsString(joinDto)).with(httpBasic(player.getEmail(),
				DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_ALL_FIELD_EMPTY_CODE)));
	}

	@Test
	public void joinGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom createdGameRoom = createTestGameRoom(player, ATTACKER);

		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(ATTACKER);

		// Second player join game room
		ResultActions resultActions = mockMvc.perform(patch(String.format(JOIN_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(joinDto)).with(httpBasic
						(secPlayer.getEmail(), SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(SEC_PLAYER_EMAIL_CONTAIN_PATH, is(not(empty()))));
	}

	@Test
	public void joinGameRoomWithDupGameRoleException() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom createdGameRoom = createTestGameRoom(player, DEFENDER);

		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(DEFENDER);

		// Second player join game room
		ResultActions resultActions = mockMvc.perform(patch(String.format(JOIN_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(joinDto)).with(httpBasic
						(secPlayer.getEmail(), SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(DUP_GAME_ROLE_CODE)));
	}

	@Test
	public void joinGameRoomWithDtoBindingError() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom createdGameRoom = createTestGameRoom(player, ATTACKER);

		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();

		// Second player join game room
		ResultActions resultActions = mockMvc.perform(patch(String.format(JOIN_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(joinDto)).with(httpBasic
						(secPlayer.getEmail(), SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_BAD_REQUEST_CODE)));
	}

	@Test
	public void ownerChange() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom createdGameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		GameRoomDto.ChangeOwner changeOwnerDto = new GameRoomDto.ChangeOwner();
		changeOwnerDto.setNewOwnerId(secPlayer.getId());
		changeOwnerDto.setOldOwnerId(player.getId());

		// Change game room owner (Change owner action's permit only current owner.)
		ResultActions resultActions = mockMvc.perform(patch(String.format(CHANGE_OWNER_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(changeOwnerDto)).with(httpBasic
						(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(OWNER_EMAIL_PATH, is(secPlayer.getEmail())));
	}

	@Test
	public void ownerChangeWithDtoBindingError() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom createdGameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		GameRoomDto.ChangeOwner changeOwnerDto = new GameRoomDto.ChangeOwner();
		changeOwnerDto.setOldOwnerId(player.getId());

		// Change game room owner (Change owner action's permit only current owner.)
		ResultActions resultActions = mockMvc.perform(patch(String.format(CHANGE_OWNER_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(changeOwnerDto)).with(httpBasic
						(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_BAD_REQUEST_CODE)));
	}

	@Test
	public void normalPlayerTryOwnerChangeWithOwnerChangeException() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom createdGameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		GameRoomDto.ChangeOwner changeOwnerDto = new GameRoomDto.ChangeOwner();
		changeOwnerDto.setNewOwnerId(secPlayer.getId());
		changeOwnerDto.setOldOwnerId(player.getId());

		// Change game room owner (Change owner action's permit only current owner.)
		ResultActions resultActions = mockMvc.perform(patch(String.format(CHANGE_OWNER_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(changeOwnerDto)).with(httpBasic
						(secPlayer.getEmail(), SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(OWNER_CHANGE_EX_CODE)));
	}

	@Test
	public void deleteGameRoom() throws Exception {
		Player player = creator.createTestAdminPlayer();
		GameRoom gameRoom = createTestGameRoom(player);

		ResultActions resultActions = mockMvc.perform(delete(BASE_URL + "/" + gameRoom.getId()).with(httpBasic(player
				.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isNoContent());
	}

	@Test
	public void leaveGameRoomWithGameRoomNotFoundException() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoom gameRoom = createTestGameRoom(player);

		ResultActions resultActions = mockMvc.perform(patch(String.format(LEAVE_URL, (gameRoom.getId() + 1L))).with
				(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_NOT_FOUND_CODE)));
	}

	@Test
	public void leaveGameRoomWithGameRoomPlayerNotFoundException() throws Exception {
		Player player = creator.createTestPlayer();
		createTestGameRoom(player);

		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);
		GameRoom secGameRoom = createTestGameRoom(secPlayer);

		ResultActions resultActions = mockMvc.perform(patch(String.format(LEAVE_URL, secGameRoom.getId())).with
				(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_PLAYER_NOT_FOUND_CODE)));
	}

	@Test
	public void leaveGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom gameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		ResultActions resultActions = mockMvc.perform(patch(String.format(LEAVE_URL, gameRoom.getId())).with(httpBasic
				(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(PLAYER_EMAIL_CONTAIN_PATH, is(empty())));
	}

	@Test
	public void ownerLeaveGameRoomAndChangeOwner() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom gameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		ResultActions resultActions = mockMvc.perform(patch(String.format(LEAVE_URL, gameRoom.getId())).with(httpBasic
				(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(PLAYER_EMAIL_CONTAIN_PATH, is(empty())));
		resultActions.andExpect(jsonPath(OWNER_EMAIL_PATH, is(secPlayer.getEmail())));
	}

	@Test
	public void allPlayerLeaveGameRoomAndNotExistGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		Player secPlayer = creator.createTestPlayer(SEC_EMAIL, SEC_NICK, SEC_PASS);

		GameRoom gameRoom = createTestGameRoomAndJoinGameRoom(player, secPlayer);

		// First player leave the game room
		ResultActions resultActions = mockMvc.perform(patch(String.format(LEAVE_URL, gameRoom.getId())).with(httpBasic
				(player.getEmail(), DEFAULT_TEST_PASS)));
		resultActions.andDo(print());

		// Second player leave the game room
		resultActions = mockMvc.perform(delete(String.format(LEAVE_URL, gameRoom.getId())).with(httpBasic(secPlayer
				.getEmail(), SEC_PASS)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isNoContent());

		// All players leave the game room then game room not found
		resultActions = mockMvc.perform(get(BASE_URL + "/" + gameRoom.getId()).with(httpBasic(secPlayer.getEmail(),
				SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(GAMEROOM_NOT_FOUND_CODE)));
	}

	private GameRoom createTestGameRoom(Player player) throws Exception {
		return createTestGameRoom(player, ATTACKER);
	}

	private GameRoom createTestGameRoom(Player player, GameRole gameRole) throws Exception {
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(gameRole);

		// Create new game room
		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), DEFAULT_TEST_PASS)));

		resultActions.andDo(print());
		String result = resultActions.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(result, GameRoom.class);
	}

	private GameRoom createTestGameRoomAndJoinGameRoom(Player player, Player secPlayer) throws Exception {
		GameRoom createdGameRoom = createTestGameRoom(player);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(ATTACKER);

		// Second player join game room
		ResultActions resultActions = mockMvc.perform(patch(String.format(JOIN_URL, createdGameRoom.getId()))
				.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(joinDto)).with(httpBasic
						(secPlayer.getEmail(), SEC_PASS)));
		resultActions.andDo(print());

		String result = resultActions.andReturn().getResponse().getContentAsString();
		return objectMapper.readValue(result, GameRoom.class);
	}
}
