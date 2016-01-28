package kr.co.leehana.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:46
 */
@Entity
@Data
public class MatchRecord {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	private TotalGame totalGame;
	@OneToOne
	private Win win;
	@OneToOne
	private Lose lose;
}
