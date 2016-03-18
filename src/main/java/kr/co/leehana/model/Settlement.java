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
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2015-12-20 16:01
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class Settlement implements Serializable {

	private static final long serialVersionUID = 4024936282233022005L;

	@Id
	@GeneratedValue
	@Column(name = "settlement_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	private Boolean solved;

	public Settlement(Boolean solved) {
		this.solved = solved;
	}
}
