package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Hana Lee
 * @since 2016-01-03 20:49
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Rank {

	@Id
	@GeneratedValue
	private long id;

	private int ranking;

	public Rank(int ranking) {
		this.ranking = ranking;
	}
}
