package kr.co.leehana.dto;

import kr.co.leehana.annotation.PasswordMatches;
import kr.co.leehana.annotation.ValidEmail;
import kr.co.leehana.annotation.ValidGuessNumber;
import kr.co.leehana.enums.Enabled;
import kr.co.leehana.enums.GameRole;
import kr.co.leehana.enums.Status;
import kr.co.leehana.model.AttackerRoleCount;
import kr.co.leehana.model.Avatar;
import kr.co.leehana.model.DefenderRoleCount;
import kr.co.leehana.model.Level;
import kr.co.leehana.model.MatchRecord;
import kr.co.leehana.model.TotalRank;
import kr.co.leehana.model.TotalScore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:13
 */
public class PlayerDto {

	@PasswordMatches
	@Data
	public static class Create {

		@NotBlank
		@ValidEmail
		private String email;

		@NotBlank
		@Size(min = 2, max = 20)
		private String nickname;

		@NotBlank
		@Size(min = 4, max = 41)
		private String password;
		private String matchingPassword;

		private Boolean admin;

//		private Avatar avatar;
	}

	@Data
	public static class Response {
		private Long id;
		private String nickname;
		private String email;
		private Avatar avatar;
		private Level level;
		private TotalScore totalScore;
		private TotalRank totalRank;
		private MatchRecord matchRecord;
		private DefenderRoleCount defenderRoleCount;
		private AttackerRoleCount attackerRoleCount;
		private GameRole gameRole;
		private Boolean admin;
		private Status status;
		private Date joined;
		private Date updated;
		private Date deleted;
		private Enabled enabled;
		private Integer inputCount;
		private String guessNumber;
	}

	@Data
	@ValidGuessNumber
	public static class Update {
		private Long gameRoomId;
		private Long id;
		private String nickname;
		private String password;
		private String email;
		private Avatar avatar;
		private Level level;
		private TotalScore totalScore;
		private TotalRank totalRank;
		private MatchRecord matchRecord;
		private DefenderRoleCount defenderRoleCount;
		private AttackerRoleCount attackerRoleCount;
		private GameRole gameRole;
		private Boolean admin;
		private Status status;
		private Integer inputCount;
		private String guessNumber;
		private Date gameOverTime;
	}

	@Data
	public static class Message {
		private String clientId;
		private String operation;
		private String id;
		private PlayerDto.Response data;
	}

	@Data
	public static class Ready {
		private Status status;
	}
}
