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
 * @since 2016-01-28 15:51
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Level implements Serializable {

	private static final long serialVersionUID = 9042217050310930942L;

	@Id
	@GeneratedValue
	@Column(name = "level_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private Integer value;

	public Level(Integer value) {
		this.value = value;
	}
}
