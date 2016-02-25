package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-02-25 21:16
 */
//@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Chat implements Serializable {

	private static final long serialVersionUID = 3055921412133797685L;

	private String id;
	private String email;
	private String user;
	// 채팅 메세지
	private String value;
}
