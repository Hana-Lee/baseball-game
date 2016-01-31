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
public class AttackerRoleCount {

	@Id
	@GeneratedValue
	@Column(name = "attacker_role_count_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotNull
	private Integer value;

	public AttackerRoleCount(Integer value) {
		this.value = value;
	}
}
