package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.GameRoomDto;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import kr.co.leehana.service.GameRoomService;
import kr.co.leehana.type.GameRole;
import kr.co.leehana.utils.TestPlayerCreator;
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

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private Filter springSecurityFilterChain;

	@Autowired
	private GameRoomService gameRoomService;

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
		createDto.setName("루비");
		createDto.setSetting(setting);
		createDto.setGameRole(GameRole.ATTACKER);

		ResultActions resultActions = mockMvc.perform(post("/gameroom").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)).with(httpBasic(player.getEmail(),
						TestPlayerCreator.DEFAULT_TEST_PASS)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.name", is("루비")));
		resultActions.andExpect(jsonPath("$.owner.email", is(player.getEmail())));
		resultActions.andExpect(jsonPath("$.owner.gameRole", is(GameRole.ATTACKER.name())));
		resultActions.andExpect(jsonPath("$.players[0].email", is(player.getEmail())));
	}
}
