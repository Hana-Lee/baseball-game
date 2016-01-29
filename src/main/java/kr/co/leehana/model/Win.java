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
public class Win extends BasicModel implements Serializable {

	private static final long serialVersionUID = 6369613778367521083L;

	private int count;

	public Win(int count) {
		this.count = count;
	}
}
