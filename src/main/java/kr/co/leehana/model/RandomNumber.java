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
 * @since 2016-03-11 20:36
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class RandomNumber implements Serializable {

	private static final long serialVersionUID = 8956584647637765700L;

	@Id
	@GeneratedValue
	@Column(name = "random_number_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	private String value;

	public RandomNumber(String value) {
		this.value = value;
	}
}
