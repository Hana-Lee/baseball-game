package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {

	private GuessNumberComparedResult result;
	private OldUser user;
	private OldGameRoom gameRoom;
	private Score score;
	private ErrorMessage errorMessage;
}
