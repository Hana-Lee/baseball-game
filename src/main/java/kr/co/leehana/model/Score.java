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
 * @since 2015-12-23 22:40
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Score implements Serializable {

	private static final long serialVersionUID = 5712657071078176370L;

	@Id
	@GeneratedValue
	@Column(name = "score_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	private Integer value;

	public Score(Integer value) {
		this.value = value;
	}
}
