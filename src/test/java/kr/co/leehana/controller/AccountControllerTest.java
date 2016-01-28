package kr.co.leehana.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.dto.AccountDto;
import kr.co.leehana.model.Account;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

	private static final String TEST_URL = "/accounts";
	private static final String TEST_EMAIL = "email@email.co.kr";
	private static final String TEST_NICKNAME = "이하나";
	private static final String TEST_PASSWORD = "password";
	private static final String TEST_EMPTY_STR = " ";
	private static final String DUP_ERROR_CODE = "duplicated.email.exception";
	private static final String TEST_SHORT_NICK = "1";
	private static final String TEST_LONG_NICK = "123456789012345678901";
	private static final String TEST_LONG_PASS = "123456789012345678901234567890123456789012";
	private static final String TEST_SHORT_PASS = "123";

	private static final String EMAIL_PATH = "$.email";
	private static final String ERROR_CODE_PATH = "$.errorCode";

	private static final String[] TEST_WRONG_EMAILS = {"a", "a@", "a@a", "a@2.컴"};

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain)
				.build();
	}

	@Test
	public void createAccount() throws Exception {
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_PASSWORD);

		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
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
	public void createAccountEmptyEmailBadRequest() throws Exception {
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMPTY_STR, TEST_NICKNAME, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountWrongEmailBadRequest() throws Exception {
		for (String wrongEmail : TEST_WRONG_EMAILS) {
			AccountDto.Create createDto = accountCreateDtoFixture(wrongEmail, TEST_NICKNAME, TEST_PASSWORD);
			assertBadRequest(createDto);
		}
	}

	@Test
	public void createAccountEmptyNicknameBadRequest() throws Exception {
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_EMPTY_STR, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountShortNicknameBadRequest() throws Exception {
		// min = 2
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_SHORT_NICK, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountLongNicknameBadRequest() throws Exception {
		// max = 20
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_LONG_NICK, TEST_PASSWORD);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountEmptyPasswordBadRequest() throws Exception {
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_EMPTY_STR);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountShortPasswordBadRequest() throws Exception {
		// min = 4
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_SHORT_PASS);
		assertBadRequest(createDto);
	}

	@Test
	public void createAccountLongPasswordBadRequest() throws Exception {
		// max = 41
		AccountDto.Create createDto = accountCreateDtoFixture(TEST_EMAIL, TEST_NICKNAME, TEST_LONG_PASS);
		assertBadRequest(createDto);
	}

	private void assertBadRequest(AccountDto.Create createDto) throws Exception {
		ResultActions resultActions = mockMvc.perform(post(TEST_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccounts() throws Exception {
		ResultActions resultActions = mockMvc.perform(get(TEST_URL));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
	}

	private AccountDto.Create accountCreateDtoFixture(String email, String nickname, String password) {
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setEmail(email);
		createDto.setNickname(nickname);
		createDto.setPassword(password);
		return createDto;
	}
}
