package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.Level;
import kr.co.leehana.model.Lose;
import kr.co.leehana.model.MatchRecord;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.TotalGame;
import kr.co.leehana.model.TotalRank;
import kr.co.leehana.model.Win;
import kr.co.leehana.service.PlayerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Hana Lee
 * @since 2016-01-28 20:46
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Transactional
public class PlayerControllerTest {

	private static final String TEST_URL = "/players";
	private static final String TEST_EMAIL = "email@email.co.kr";
	private static final String TEST_NICKNAME = "이하나";
	private static final String TEST_UP_NICKNAME = "이두나";
	private static final String TEST_PASSWORD = "password";
	private static final String TEST_EMPTY_STR = " ";
	private static final String DUP_ERROR_CODE = "duplicated.email.exception";
	private static final String PLAYER_NOT_FOUND_ERROR_CODE = "player.not.found.exception";
	private static final String TEST_SHORT_NICK = "1";
	private static final String TEST_LONG_NICK = "123456789012345678901";
	private static final String TEST_SHORT_PASS = "123";
	private static final String TEST_LONG_PASS = "123456789012345678901234567890123456789012";
	private static final String[] TEST_WRONG_EMAILS = {"a", "a@", "a@a", "a@2.컴"};
	private static final String EMAIL_PATH = "$.email";
	private static final String ERROR_CODE_PATH = "$.errorCode";
	private static final String NICKNAME_PATH = "$.nickname";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private Filter springSecurityFilterChain;

	private ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain)
				.build();
	}

	@Test
	public void createPlayer() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		//{"id":1,"username":"voyaging","email":null,"fullName":null,"joined":1444821003172,"updated":1444821003172}
		resultActions.andExpect(jsonPath(EMAIL_PATH, is(TEST_EMAIL)));
	}

	@Test
	public void createPlayerWithDupError() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		//{"id":1,"username":"voyaging","email":null,"fullName":null,"joined":1444821003172,"updated":1444821003172}
		resultActions.andExpect(jsonPath(EMAIL_PATH, is(TEST_EMAIL)));

		resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper
				.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		//{"message":"[voyaging] 중복된 username 입니다.","errorCode":"duplicated.username.exception","errors":null}
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(DUP_ERROR_CODE)));
	}

	@Test
	public void createPlayerEmptyEmailBadRequest() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMPTY_STR, TEST_NICKNAME, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerWrongEmailBadRequest() throws Exception {
		for (String wrongEmail : TEST_WRONG_EMAILS) {
			PlayerDto.Create createDto = playerCreateDtoFixture(wrongEmail, TEST_NICKNAME, TEST_PASSWORD);
			assertBadRequest(createDto);
		}
	}

	@Test
	public void createPlayerEmptyNicknameBadRequest() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_EMPTY_STR, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerShortNicknameBadRequest() throws Exception {
		// min = 2
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_SHORT_NICK, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerLongNicknameBadRequest() throws Exception {
		// max = 20
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_LONG_NICK, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerEmptyPasswordBadRequest() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_EMPTY_STR);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerShortPasswordBadRequest() throws Exception {
		// min = 4
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_SHORT_PASS);
		assertBadRequest(createDto);
	}

	@Test
	public void createPlayerLongPasswordBadRequest() throws Exception {
		// max = 41
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_LONG_PASS);
		assertBadRequest(createDto);
	}

	private void assertBadRequest(PlayerDto.Create createDto) throws Exception {
		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON).content
				(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void getPlayers() throws Exception {
		ResultActions resultActions = mockMvc.perform(get(TEST_URL));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
	}

	@Test
	public void getPlayer() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);
		Player newPlayer = playerService.create(createDto);

		ResultActions resultActions = mockMvc.perform(get(TEST_URL + "/" + newPlayer.getId()));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(EMAIL_PATH, is(TEST_EMAIL)));
		resultActions.andExpect(jsonPath(NICKNAME_PATH, is(TEST_NICKNAME)));
	}

	@Test
	public void notExistPlayerGet() throws Exception {
		ResultActions resultActions = mockMvc.perform(get(TEST_URL + "/1"));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		resultActions.andExpect(jsonPath(ERROR_CODE_PATH, is(PLAYER_NOT_FOUND_ERROR_CODE)));
	}

	@Test
	public void updatePlayer() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);
		Player newPlayer = playerService.create(createDto);

		PlayerDto.Update updateDto = new PlayerDto.Update();
		updateDto.setEmail(TEST_EMAIL);
		updateDto.setNickname(TEST_UP_NICKNAME);
		updateDto.setPassword(TEST_PASSWORD);

		updateDto.setLevel(new Level(2));

		MatchRecord matchRecord = new MatchRecord(new TotalGame(1), new Win(1), new Lose(0));
		updateDto.setMatchRecord(matchRecord);
		updateDto.setTotalRank(new TotalRank(1));

		ResultActions resultActions = mockMvc.perform(put(TEST_URL + "/" + newPlayer.getId()).contentType(MediaType
				.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath(NICKNAME_PATH, is(TEST_UP_NICKNAME)));
		resultActions.andExpect(jsonPath("$.level.value", is(2)));
		resultActions.andExpect(jsonPath("$.matchRecord.totalGame.count", is(1)));
		resultActions.andExpect(jsonPath("$.matchRecord.win.count", is(1)));
		resultActions.andExpect(jsonPath("$.matchRecord.lose.count", is(0)));
		resultActions.andExpect(jsonPath("$.totalRank.value", is(1)));
	}

	@Test
	public void deletePlayer() throws Exception {
		PlayerDto.Create createDto = playerCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);
		Player newPlayer = playerService.create(createDto);

		ResultActions resultActions = mockMvc.perform(delete(TEST_URL + "/" + newPlayer.getId()));
		resultActions.andDo(print());
		resultActions.andExpect(status().isNoContent());
	}

	private PlayerDto.Create playerCreateDtoFixture(String email, String nickname, String password) {
		PlayerDto.Create createDto = new PlayerDto.Create();
		createDto.setEmail(email);
		createDto.setNickname(nickname);
		createDto.setPassword(password);
		return createDto;
	}
}
