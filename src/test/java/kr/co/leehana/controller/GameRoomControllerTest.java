package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.service.PlayerService;
import kr.co.leehana.type.GameRole;
import kr.co.leehana.utils.TestPlayerCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static kr.co.leehana.utils.CommonsTestConstant.ERROR_CODE_PATH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Hana Lee
 * @since 2016-02-09 18:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(App.class)
@WebAppConfiguration
@Transactional
public class GameRoomControllerTest {

	private static final String TEST_URL = "/gameroom";
	private static final String TEST_JOIN_URL = "/gameroom/join/";
	private static final String TEST_ROOM_NAME = "루비";
	private static final String DUP_ERROR_CODE = "duplicated.owner.exception";
	private static final String DUP_GAME_ROLE_CODE = "duplicated.gameRole.exception";
	private static final String TEST_SEC_EMAIL = "i2@leehana.co.kr";
	private static final String TEST_SEC_NICK = "이하나2";
	private static final String TEST_SEC_PASS = "dlgksk";

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
	public void createGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(TEST_ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(GameRole.ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), TestPlayerCreator
				.DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.name", is(TEST_ROOM_NAME)));
		resultActions.andExpect(jsonPath("$.owner.email", is(player.getEmail())));
		resultActions.andExpect(jsonPath("$.owner.gameRole", is(GameRole.ATTACKER.name())));
		resultActions.andExpect(jsonPath("$.players[0].email", is(player.getEmail())));
	}

	@Test
	public void createGameRoomWithDupOwnerException() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(TEST_ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(GameRole.ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), TestPlayerCreator
				.DEFAULT_TEST_PASS)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());

		resultActions = mockMvc.perform(post(TEST_URL).contentType(APPLICATION_JSON).content(objectMapper
				.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), TestPlayerCreator
				.DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(DUP_ERROR_CODE)));
	}

	@Test
	public void joinGameRoom() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(TEST_ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(GameRole.ATTACKER);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), TestPlayerCreator
				.DEFAULT_TEST_PASS)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());

		String result = resultActions.andReturn().getResponse().getContentAsString();
		GameRoom createdGameRoom = objectMapper.readValue(result, GameRoom.class);

		creator.createTestPlayer(TEST_SEC_EMAIL, TEST_SEC_NICK, TEST_SEC_PASS);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(GameRole.ATTACKER);

		resultActions = mockMvc.perform(post(TEST_JOIN_URL + createdGameRoom.getId()).contentType(APPLICATION_JSON).content(objectMapper
				.writeValueAsString(joinDto)).with(httpBasic(TEST_SEC_EMAIL, TEST_SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.players[?(@.email == 'i2@leehana.co.kr')]", is(not(empty()))));
	}

	@Test
	public void joinGameRoomWithDupGameRoleException() throws Exception {
		Player player = creator.createTestPlayer();
		GameRoomDto.Create createDto = new GameRoomDto.Create();
		createDto.setName(TEST_ROOM_NAME);
		createDto.setSetting(setting);
		createDto.setGameRole(GameRole.DEFENDER);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(), TestPlayerCreator
				.DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());

		String result = resultActions.andReturn().getResponse().getContentAsString();
		GameRoom createdGameRoom = objectMapper.readValue(result, GameRoom.class);

		creator.createTestPlayer(TEST_SEC_EMAIL, TEST_SEC_NICK, TEST_SEC_PASS);

		GameRoomDto.Join joinDto = new GameRoomDto.Join();
		joinDto.setGameRole(GameRole.DEFENDER);

		resultActions = mockMvc.perform(post(TEST_JOIN_URL + createdGameRoom.getId()).contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(joinDto)).with(httpBasic(TEST_SEC_EMAIL, TEST_SEC_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(DUP_GAME_ROLE_CODE)));
	}
}
