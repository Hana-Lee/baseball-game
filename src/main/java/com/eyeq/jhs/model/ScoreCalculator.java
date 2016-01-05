package com.eyeq.jhs.model;

import com.eyeq.jhs.type.RoleType;

public class ScoreCalculator {

	public ScoreCalculator() {
	}

	/**
	 * 게임이 종료된 후 수비자의 점수를 계산한다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	public static Score dependerScore(GameRoom gameRoom) {
		int totalScore;

		final Setting setting = gameRoom.getSetting();
		final int guessInputCount = setting.getLimitGuessInputCount();
		final int generationNumberCount = setting.getGenerationNumberCount();

		final long totalUsers = gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(RoleType
				.ATTACKER)).count();
		final long totalSolvedUsers = gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals
				(RoleType.ATTACKER) && u.getResult().getSolve().isValue()).count();
		final float baseScore = 40 * totalUsers - (totalSolvedUsers * 20);

		float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, false);
		float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, false);

		totalScore = Math.round(guessScoreValue + numberCountScoreValue);

		return new Score(totalScore);
	}

	/**
	 * 유저의 게임이 종료된 후 각각의 유저별로 계산 된다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param result   게임의 결과 객체
	 * @param user     게임 유저
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	public static Score attackerScore(Result result, User user, GameRoom gameRoom) {
		int totalScore = 0;

		final Setting setting = gameRoom.getSetting();

		if (result != null && user.getWrongCount() < setting.getLimitWrongInputCount()) {
			final Role role = user.getRole();
			final int guessInputCount = setting.getLimitGuessInputCount();
			final int generationNumberCount = setting.getGenerationNumberCount();

			if (result.getSolve().isValue() && (user.getRank() != null && user.getRank().getRanking() > 0)) {
				if (role.getRoleType().equals(RoleType.ATTACKER)) {
					final int totalUsers = gameRoom.getUsers().size();
					final Rank rank = user.getRank();
					final float baseScore = 20 * totalUsers - ((rank.getRanking() - 1) * 10);

					float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, true);

					float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, true);

					totalScore = Math.round(guessScoreValue + numberCountScoreValue);
				}
			} else if (!result.getSolve().isValue() && setting.getLimitGuessInputCount() == user.getGuessCount() &&
					role.getRoleType().equals(RoleType.ATTACKER)) {
				// 추측가능 횟수에 도달하였으나 숫자를 못맞춘경우
				final float baseScore = 5;

				float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, true);

				float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, true);

				totalScore = Math.round(guessScoreValue + numberCountScoreValue);

			} else if (!result.getSolve().isValue() && user.getWrongCount() == setting.getLimitWrongInputCount() &&
					role.getRoleType().equals(RoleType.ATTACKER)) {
				// 입력 오류 횟수 도달시
				totalScore = 0;
			}
		}

		return new Score(totalScore);
	}

	private static float getNumberCountScoreValue(int generationNumberCount, float baseScore, boolean isAttacker) {
		float numberCountScoreValue;
		switch (generationNumberCount) {
			case 2:
				if (isAttacker) {
					numberCountScoreValue = baseScore / 2.0f;
				} else {
					numberCountScoreValue = baseScore * 2.0f;
				}
				break;
			case 3:
				numberCountScoreValue = baseScore;
				break;
			case 4:
				if (isAttacker) {
					numberCountScoreValue = baseScore * 2.0f;
				} else {
					numberCountScoreValue = baseScore / 2.0f;
				}
				break;
			case 5:
				if (isAttacker) {
					numberCountScoreValue = baseScore * 2.0f * 2.0f;
				} else {
					numberCountScoreValue = baseScore / 2.0f / 2.0f;
				}
				break;
			default:
				numberCountScoreValue = 0.0f;
				break;
		}
		return numberCountScoreValue;
	}

	private static float getGuessScoreValue(int guessInputCount, float baseScore, boolean isAttacker) {
		float guessScoreValue;
		switch (guessInputCount) {
			case 20:
				if (isAttacker) {
					guessScoreValue = baseScore / 1.5f / 1.5f;
				} else {
					guessScoreValue = baseScore * 1.5f * 1.5f;
				}
				break;
			case 15:
				if (isAttacker) {
					guessScoreValue = baseScore / 1.5f;
				} else {
					guessScoreValue = baseScore * 1.5f;
				}
				break;
			case 10:
				guessScoreValue = baseScore;
				break;
			case 5:
				if (isAttacker) {
					guessScoreValue = baseScore * 1.5f;
				} else {
					guessScoreValue = baseScore / 1.5f;
				}
				break;
			case 1:
				if (isAttacker) {
					guessScoreValue = baseScore * 1.5f * 1.5f;
				} else {
					guessScoreValue = baseScore / 1.5f / 1.5f;
				}
				break;
			default:
				guessScoreValue = 0.0f;
				break;
		}
		return guessScoreValue;
	}
}