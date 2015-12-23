package com.eyeq.jhs.model;

public class ScoreCalculator {
	public ScoreCalculator() {
	}

	public static int calculateScore(int nthGame, Result result) {
		int totalScore = 0;

		if (result != null && result.getSolve().isValue()) {
			if (nthGame <= 10) {
				totalScore = 1000 - (100 * (nthGame - 1));
			}
		}

		// setting 객체에서 총 점수 셋팅값 가져와서 점수계산 리팩토

		return totalScore;
	}
}