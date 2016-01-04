package com.eyeq.jhs.model;

import com.eyeq.jhs.type.RoleType;

public class ScoreCalculator {

	public ScoreCalculator() {
	}

	public static Score calculateScore(Result result, User user, GameRoom gameRoom) {
		int totalScore = 0;

		final Setting setting = gameRoom.getSetting();

		if (result != null && user.getWrongCount() < setting.getLimitWrongInputCount()) {
			final int totalUsers = gameRoom.getUsers().size();
			final Role role = user.getRole();
			final int guessInputCount = setting.getLimitGuessInputCount();
			final int generationNumberCount = setting.getGenerationNumberCount();

			if (result.getSolve().isValue() && (user.getRank() != null && user.getRank().getRanking() > 0)) {
				if (role.getRoleType().equals(RoleType.ATTACKER)) {
					final Rank rank = user.getRank();
					final float baseScore = 20 * totalUsers - ((rank.getRanking() - 1) * 10);

					float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore);

					float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore);

					totalScore = Math.round(guessScoreValue + numberCountScoreValue);
				} else if (role.getRoleType().equals(RoleType.DEPENDER)) {

				}
			} else if (!result.getSolve().isValue() && setting.getLimitGuessInputCount() == user.getGuessCount() &&
					role.getRoleType().equals(RoleType.ATTACKER)) {
				// 추측가능 횟수에 도달하였으나 숫자를 못맞춘경우
				final float baseScore = 5;

				float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore);

				float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore);

				totalScore = Math.round(guessScoreValue + numberCountScoreValue);

			} else if (!result.getSolve().isValue() && user.getWrongCount() == setting.getLimitWrongInputCount() &&
					role.getRoleType().equals(RoleType.ATTACKER)) {
				// 입력 오류 횟수 도달시
				totalScore = 0;
			}
		}

		return new Score(totalScore);
	}

	private static float getNumberCountScoreValue(int generationNumberCount, float baseScore) {
		float numberCountScoreValue;
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
		return numberCountScoreValue;
	}

	private static float getGuessScoreValue(int guessInputCount, float baseScore) {
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
		return guessScoreValue;
	}
}