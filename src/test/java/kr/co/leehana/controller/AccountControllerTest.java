package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.service.AccountService;
import lombok.extern.slf4j.Slf4j;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@Slf4j
public class AccountControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AccountService accountService;

	@Autowired
	private Filter springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain)
				.build();
	}

	@Test
	public void createAccount() throws Exception {
		AccountDto.Create createDto = accountCreateDtoFixture();

		ResultActions resultActions = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		//{"id":1,"username":"voyaging","email":null,"fullName":null,"joined":1444821003172,"updated":1444821003172}
		resultActions.andExpect(jsonPath("$.email", is("v@leehana.co.kr")));

		resultActions = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper
				.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		//{"message":"[voyaging] 중복된 username 입니다.","errorCode":"duplicated.username.exception","errors":null}
		resultActions.andExpect(jsonPath("$.errorCode", is("duplicated.email.exception")));
	}

	private AccountDto.Create accountCreateDtoFixture() {
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setEmail("v@leehana.co.kr");
		createDto.setNickname("이하나");
		createDto.setPassword("password");
		return createDto;
	}
}
