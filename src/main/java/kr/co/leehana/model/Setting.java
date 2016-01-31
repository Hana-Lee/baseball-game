package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-31 21:20
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class Setting implements Serializable {

	private static final long serialVersionUID = 4139315805048136842L;

	@Id
	@GeneratedValue
	@Column(name = "setting_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	//잘못된 숫자 입력 횟수 제한
	private Integer limitWrongInputCount = 5;

	// 야구 게임 횟수
	private Integer limitGuessInputCount = 10;

	// 생성 숫자 갯수
	private Integer generationNumberCount = 3;

	public Setting(Integer limitWrongInputCount, Integer limitGuessInputCount, Integer generationNumberCount) {
		this.limitGuessInputCount = limitGuessInputCount;
		this.limitWrongInputCount = limitWrongInputCount;
		this.generationNumberCount = generationNumberCount;
	}
}