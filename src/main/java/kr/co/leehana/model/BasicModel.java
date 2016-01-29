package kr.co.leehana.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Hana Lee
 * @since 2016-01-29 17:11
 */
@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
public abstract class BasicModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
}
