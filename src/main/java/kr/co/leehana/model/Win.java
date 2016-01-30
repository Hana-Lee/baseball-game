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
public class Win implements Serializable {

	private static final long serialVersionUID = 6369613778367521083L;

	@Id
	@GeneratedValue
	@Column(name = "win_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private Integer count;

	public Win(Integer count) {
		this.count = count;
	}
}
