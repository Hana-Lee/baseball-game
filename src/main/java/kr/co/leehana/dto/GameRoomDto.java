package kr.co.leehana.dto;

import kr.co.leehana.model.Player;
import kr.co.leehana.model.Setting;
import kr.co.leehana.type.GameRole;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
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
		private GameRole gameRole;

		private Player owner;

		@NotNull
		private Setting setting;
	}

	@Data
	public static class Update {
		private String name;
		private Player owner;
		private Set<Player> players;
		private Setting setting;
		private Integer gameCount;
		private Map<Integer, Player> playerRankMap;
	}

	@Data
	public static class Response {
		private Integer number;
		private String name;
		private Integer limitPlayerCount;
		private Player owner;
		private Set<Player> players;
		private Setting setting;
		private Integer gameCount;
		private Map<Integer, Player> playerRankMap;
		private Date created;
		private Date updated;
	}

	@Data
	public static class Join {
		private GameRole gameRole;
	}
}
