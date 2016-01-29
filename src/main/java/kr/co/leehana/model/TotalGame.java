package kr.co.leehana.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:49
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class TotalGame extends BasicModel implements Serializable {

	private static final long serialVersionUID = -8212336561968512803L;

	private int count;

	public TotalGame(int count) {
		this.count = count;
	}
}