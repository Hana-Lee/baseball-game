package kr.co.leehana.dto;

import kr.co.leehana.model.Chat;
import lombok.Data;

/**
 * @author Hana Lee
 * @since 2016-02-25 21:17
 */
public class ChatDto {

	@Data
	public static class Message {
		private String clientId;
		private String operation;
		private String id;
		private Chat data;
	}
}
