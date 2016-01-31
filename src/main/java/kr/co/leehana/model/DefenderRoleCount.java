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
 * @since 2016-01-31 19:19
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class DefenderRoleCount implements Serializable {

	private static final long serialVersionUID = 3306994590182325940L;

	@Id
	@GeneratedValue
	@Column(name = "defender_role_count_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private Integer value;

	public DefenderRoleCount(Integer value) {
		this.value = value;
	}
}
