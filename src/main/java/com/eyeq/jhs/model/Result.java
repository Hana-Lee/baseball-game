package com.eyeq.jhs.model;

public class Result {

	private boolean isSolved;
	private int strikeCount;
	private int ballCount;
	private Strike strike;
	private Ball ball;

	public Result(boolean isSolved, Strike strike, Ball ball) {
		this.isSolved = isSolved;
		this.strikeCount = strike.getCount();
		this.ballCount = ball.getCount();
		this.strike = strike;
		this.ball = ball;

	}

	public Strike getStrike() {
		return strike;
	}

	public Ball getBall() {
		return ball;
	}

	public boolean isSolved() {
		return isSolved;
	}

	public int getStrikeCount() {
		return strikeCount;
	}

	public int getBallsCount() {
		return ballCount;
	}

}
