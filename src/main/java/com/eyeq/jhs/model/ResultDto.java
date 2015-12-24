package com.eyeq.jhs.model;

import lombok.Data;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:39
 */
@Data
public class ResultDto {

	private Result result;
	private User user;
	private GameRoom gameRoom;
	private Score score;
	private ErrorMessage errorMessage;
}
