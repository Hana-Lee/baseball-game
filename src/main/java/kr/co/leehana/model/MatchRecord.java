package kr.co.leehana.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2016-01-28 15:46
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class MatchRecord implements Serializable {

	private static final long serialVersionUID = -3739036334295763797L;

	@Id
	@GeneratedValue
	@Column(name = "match_record_id")
	@Setter(AccessLevel.NONE)
	private long id;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "total_game_id")
	@NotNull
	private TotalGame totalGame;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "win_id")
	@NotNull
	private Win win;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "lose_id")
	@NotNull
	private Lose lose;

	public MatchRecord(TotalGame totalGame, Win win, Lose lose) {
		this.totalGame = totalGame;
		this.win = win;
		this.lose = lose;
	}
}
