package kr.co.leehana.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-03 20:49
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Data
public class Rank extends BasicModel implements Serializable {

	private static final long serialVersionUID = -1100214892674326181L;

	private int ranking;

	public Rank(int ranking) {
		this.ranking = ranking;
	}
}
