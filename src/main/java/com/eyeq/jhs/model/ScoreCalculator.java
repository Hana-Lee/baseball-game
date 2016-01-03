package com.eyeq.jhs.model;

import com.eyeq.jhs.type.RoleType;

public class ScoreCalculator {

	public ScoreCalculator() {
	}

	public static Score calculateScore(Result result, User user, GameRoom gameRoom) {
		int totalScore = 0;

		if (result.getSolve().isValue() && (user.getRank() != null || user.getRank().getRanking() > 0)) {
			final Setting setting = gameRoom.getSetting();
			final int totalUsers = gameRoom.getUsers().size();
			final Role role = user.getRole();
			if (role.getRoleType().equals(RoleType.ATTACKER)) {
				final Rank rank = user.getRank();
				final float baseScore = 20 * totalUsers - ((rank.getRanking() - 1) * 10);

				final int guessInputCount = setting.getLimitGuessInputCount();
				float guessScoreValue;
				switch (guessInputCount) {
					case 20:
						guessScoreValue = baseScore / 1.5f / 1.5f;
						break;
					case 15:
						guessScoreValue = baseScore / 1.5f;
						break;
					case 10:
						guessScoreValue = baseScore;
						break;
					case 5:
						guessScoreValue = baseScore * 1.5f;
						break;
					case 1:
						guessScoreValue = baseScore * 1.5f * 1.5f;
						break;
					default:
						guessScoreValue = 0.0f;
						break;
				}

				float numberCountScoreValue;
				final int generationNumberCount = setting.getGenerationNumberCount();
				switch (generationNumberCount) {
					case 2:
						numberCountScoreValue = baseScore / 2.0f;
						break;
					case 3:
						numberCountScoreValue = baseScore;
						break;
					case 4:
						numberCountScoreValue = baseScore * 2.0f;
						break;
					case 5:
						numberCountScoreValue = baseScore * 2.0f * 2.0f;
						break;
					default:
						numberCountScoreValue = 0.0f;
						break;
				}

				totalScore = Math.round(guessScoreValue + numberCountScoreValue);
			} else if (role.getRoleType().equals(RoleType.DEPENDER)) {

			}
		}

		return new Score(totalScore);
	}
}