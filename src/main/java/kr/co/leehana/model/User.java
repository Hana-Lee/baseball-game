package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hana Lee
 * @since 2015-12-23 22:37
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "email" })
@ToString
public class User {

	@Setter(AccessLevel.NONE)
	private String email;
	private String nickname;
	private Rank totalRank;
	private MatchRecord matchRecord;
	private Level level;
	private Role role;
	private Boolean ready = false;
	private int guessCount = 0;
	private Boolean gameOver = false;
	private Rank rank;
	private int wrongCount = 0;
	private Result result;
	private boolean guessCompleted;
	private Score totalScore;
	private String guessNum;
	private Score currentScore;

	public User(String email, Role role, Score totalScore) {
		this.email = email;
		this.role = role;
		this.totalScore = totalScore;
	}
}
