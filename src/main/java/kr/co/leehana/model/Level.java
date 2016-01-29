package kr.co.leehana.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:51
 */
@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Level extends BasicModel implements Serializable {

	private static final long serialVersionUID = 9042217050310930942L;

	private int value;

	public Level(int value) {
		this.value = value;
	}
}
