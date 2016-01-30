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
 * @since 2016-01-28 15:49
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class TotalGame implements Serializable {

	private static final long serialVersionUID = -8212336561968512803L;

	@Id
	@GeneratedValue
	@Column(name = "total_game_id")
	@Setter(AccessLevel.NONE)
	private long id;

	@NotNull
	private int count;

	public TotalGame(int count) {
		this.count = count;
	}
}