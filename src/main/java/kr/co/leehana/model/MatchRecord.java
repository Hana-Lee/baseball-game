package kr.co.leehana.model;

import lombok.Data;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:46
 */
@Data
public class MatchRecord {
	private TotalGame totalGame;
	private Win win;
	private Lose lose;
}
