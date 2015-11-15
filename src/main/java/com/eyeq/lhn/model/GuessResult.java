package com.eyeq.lhn.model;

import java.io.Serializable;

/**
 * @author Hana Lee
 * @since 2015-11-11 22-03
 */
public class GuessResult implements Serializable {

	private static final long serialVersionUID = 882749425118511462L;

	private boolean solved;
	private Strike strike;
	private Ball ball;

	public GuessResult(boolean solved, Strike strike, Ball ball) {
		this.solved = solved;
		this.strike = strike;
		this.ball = ball;
	}

	public boolean isSolved() {
		return solved;
	}

	public int getStrike() {
		return strike.getCount();
	}

	public int getBall() {
		return ball.getCount();
	}
}
