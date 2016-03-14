package kr.co.leehana.dto;

import lombok.Data;

/**
 * @author Hana Lee
 * @since 2016-03-05 02:13
 */
@Data
public class MessagingDto {

	private String clientId;
	private String operation;
	private String id;
	private Object data;
	private String objectOperation;
	private Object object;
}
