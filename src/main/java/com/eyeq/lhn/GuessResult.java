package com.eyeq.lhn;

import com.eyeq.lhn.model.Ball;
import com.eyeq.lhn.model.Strike;

/**
 * @author Hana Lee
 * @since 2015-11-11 22-03
 */
public class GuessResult {

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
