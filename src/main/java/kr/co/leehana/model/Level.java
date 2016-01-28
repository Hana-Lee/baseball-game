package kr.co.leehana.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:51
 */
@Entity
@Data
public class Level {

	@Id
	@GeneratedValue
	private long id;

	private int value;
}
