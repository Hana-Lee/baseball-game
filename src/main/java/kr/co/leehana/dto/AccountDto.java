package kr.co.leehana.dto;

import kr.co.leehana.model.Level;
import kr.co.leehana.model.MatchRecord;
import kr.co.leehana.model.Rank;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:13
 */
public class AccountDto {

	@Data
	public static class Create {
		private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*" +
				"@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		@NotBlank
		@Pattern(regexp = EMAIL_PATTERN)
		private String email;

		@NotBlank
		@Size(min = 2, max = 20)
		private String nickname;

		@NotBlank
		@Size(min = 4, max = 41)
		private String password;
	}

	@Data
	public static class Response {
		private Long id;
		private String nickname;
		private String email;
		private Level level;
		private Rank totalRank;
		private MatchRecord matchRecord;
		private Date joined;
		private Date updated;
	}

	@Data
	public static class Update {
		private String email;
		private String password;
		private String nickname;
	}

	@Data
	public static class UpdateStatus {
		private Level level;
		private Rank totalRank;
		private MatchRecord matchRecord;
	}
}
