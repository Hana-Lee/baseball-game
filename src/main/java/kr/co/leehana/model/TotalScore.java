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
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-02-17 21:30
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class TotalScore implements Serializable {

	private static final long serialVersionUID = -5278670683593863351L;

	@Id
	@GeneratedValue
	@Column(name = "total_score_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private Integer value = 0;

	public TotalScore(Integer value) {
		this.value = value;
	}
}
