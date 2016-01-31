package kr.co.leehana.dto;

import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2016-01-31 20:35
 */
public class GameRoomDto {

	@Data
	public static class Create {

		@NotBlank
		@Size(min = 2, max = 20)
		private String name;

		@NotNull
		private Player owner;

		@NotNull
		private Setting setting;

//		private Player defender;

		private Set<Player> players;
	}

	@Data
	public static class Update {
		private String name;
		private Player owner;
		private Setting setting;
		private Set<Player> attacker;
		private Player defender;
	}

	@Data
	public static class Response {
		private Integer number;
		private String name;
		private Integer limitPlayerCount;
		private Player owner;
		private Set<Player> attackers;
		private Player defender;
		private Setting setting;
		private Integer gameCount;
		private Map<Integer, Player> playerRankMap;
	}
}
