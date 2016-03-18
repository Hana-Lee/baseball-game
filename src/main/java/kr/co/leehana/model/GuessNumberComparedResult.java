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

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class GuessNumberComparedResult implements Serializable {

	private static final long serialVersionUID = 6017973063306071485L;

	@Id
	@GeneratedValue
	@Column(name = "game_result_id")
	@Setter(AccessLevel.NONE)
	private Long id;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "settlement_id")
	@NotNull
	private Settlement settlement;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "strike_id")
	@NotNull
	private Strike strike;

	@OneToOne(cascade = {CascadeType.ALL}, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "ball_id")
	@NotNull
	private Ball ball;

	public GuessNumberComparedResult(Settlement settlement, Strike strike, Ball ball) {
		this.settlement = settlement;
		this.strike = strike;
		this.ball = ball;
	}
}
