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
 * @since 2016-01-03 20:49
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Rank implements Serializable {

	private static final long serialVersionUID = -1100214892674326181L;

	@Id
	@GeneratedValue
	@Column(name = "rank_id")
	@Setter(AccessLevel.NONE)
	private long id;

	@NotNull
	private int value;

	public Rank(int value) {
		this.value = value;
	}
}
