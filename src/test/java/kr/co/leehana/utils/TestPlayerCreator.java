package kr.co.leehana.utils;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.model.Player;
import kr.co.leehana.service.PlayerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Hana Lee
 * @since 2016-02-09 18:34
 */
@Component
public class TestPlayerCreator {

	private static final String DEFAULT_TEST_EMAIL = "i@leehana.co.kr";
	private static final String DEFAULT_TEST_NICK = "이하나";
	public static final String DEFAULT_TEST_PASS = "dlgksk";

	private PlayerService playerService;

	@Autowired
	public TestPlayerCreator(PlayerService playerService) {
		this.playerService = playerService;
	}

	public Player createTestAdminPlayer() {
		return createTestPlayer(null, null, null, Boolean.TRUE);
	}

	public Player createTestPlayer() {
		return createTestPlayer(null, null, null, null);
	}

	public Player createTestPlayer(String email, String nickname, String password) {
		return createTestPlayer(email, nickname, password, null);
	}

	public Player createTestPlayer(String email, String nickname, String password, Boolean isAdmin) {
		if (StringUtils.isBlank(email)) {
			email = DEFAULT_TEST_EMAIL;
		}
		if (StringUtils.isBlank(nickname)) {
			nickname = DEFAULT_TEST_NICK;
		}
		if (StringUtils.isBlank(password)) {
			password = DEFAULT_TEST_PASS;
		}

		if (isAdmin == null) {
			isAdmin = Boolean.FALSE;
		}

		PlayerDto.Create createDto = playerCreateDtoFixture(email, nickname, password, isAdmin);
		return playerService.create(createDto);
	}

	public PlayerDto.Create playerCreateDtoFixture(String email, String nickname, String password) {
		return playerCreateDtoFixture(email, nickname, password, Boolean.FALSE);
	}

	public PlayerDto.Create playerCreateDtoFixture(String email, String nickname, String password, Boolean isAdmin) {
		PlayerDto.Create createDto = new PlayerDto.Create();
		createDto.setEmail(email);
		createDto.setNickname(nickname);
		createDto.setPassword(password);
		createDto.setMatchingPassword(password);
		createDto.setAdmin(isAdmin);
		return createDto;
	}
}
