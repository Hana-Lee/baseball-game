package kr.co.leehana.model;

import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author Hana Lee
 * @since 2016-01-29 17:11
 */
@MappedSuperclass
@Getter
public abstract class BasicModel {

	@Id
	@GeneratedValue
	private long id;
}
