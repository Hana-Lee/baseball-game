package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-02-22 11:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 4597644212859886265L;

	private String clientId;
	private String email;
	private String message;
	private String operation;
	private String id;
	private ChatData data;

	@Data
	private class ChatData {
		private String id;
		private String user;
		private String value;
	}
}
