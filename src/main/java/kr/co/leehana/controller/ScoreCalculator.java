package kr.co.leehana.controller;

import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.Setting;
import kr.co.leehana.model.User;
import kr.co.leehana.type.RoleType;

public class ScoreCalculator {

	private static final int DEPENDER_BASE = 40;
	private static final int DEPENDER_EACH_USER = 20;
	private static final int ATTACKER_BASE = 20;
	private static final int ATTACKER_EACH_USER = 10;

	public ScoreCalculator() {
	}

	public static Score calculation(final User user, final GameRoom gameRoom) {
		if (user.getRole().getRoleType().equals(RoleType.ATTACKER)) {
			return attackerScore(user, gameRoom);
		} else {
			return dependerScore(user, gameRoom);
		}
	}

	/**
	 * 게임이 종료된 후 수비자의 점수를 계산한다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param user     게임 유저
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	public static Score dependerScore(final User user, final GameRoom gameRoom) {
		// TODO 수비자의 점수 계산이 잘못되는것 수정(모든 유저가 맞춘경우의 점수가 제대로 반영되지 않음)
		final float baseScore = makeBaseScore(user, gameRoom, gameRoom.getSetting());

		return new Score(scoreCalculation(user, gameRoom.getSetting(), baseScore));
	}

	private static long getSolvedUserCount(final GameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(RoleType.ATTACKER) && u
				.getResult().getSettlement().isSolved()).count();
	}

	private static long getAttackerCount(final GameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(RoleType.ATTACKER)).count();
	}

	/**
	 * 유저의 게임이 종료된 후 각각의 유저별로 계산 된다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param user     게임 유저
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	public static Score attackerScore(final User user, final GameRoom gameRoom) {
		int totalScore = 0;

		if (user.getResult() != null) {
			final Setting setting = gameRoom.getSetting();

			final float baseScore = makeBaseScore(user, gameRoom, setting);

			totalScore = scoreCalculation(user, setting, baseScore);
		}

		return new Score(totalScore);
	}

	private static float makeBaseScore(final User user, final GameRoom gameRoom, final Setting setting) {
		if (user.getRole().getRoleType().equals(RoleType.ATTACKER)) {
			return makeAttackerBaseScore(user, gameRoom, setting);
		} else if (user.getRole().getRoleType().equals(RoleType.DEPENDER)) {
			return makeDependerBaseScore(gameRoom);
		}

		return 0.0f;
	}

	private static float makeDependerBaseScore(final GameRoom gameRoom) {
		if (allUserFocusedNumber(gameRoom)) {
			return makeAllUserFocusedDependerBaseScore();
		} else {
			return makeBasicDependerBaseScore(gameRoom);
		}
	}

	private static boolean allUserFocusedNumber(final GameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(RoleType.ATTACKER) && u
				.getResult().getSettlement().isSolved()).count() == gameRoom.getUsers().stream().filter(u -> u.getRole
				().getRoleType().equals(RoleType.ATTACKER)).count();
	}

	private static float makeAttackerBaseScore(final User user, final GameRoom gameRoom, final Setting setting) {
		if (successGuess(user)) {
			// 성공적으로 숫자를 맞춘경우 랭크가 0 이상
			return makeSuccessAttackerBaseScore(user, gameRoom);
		} else if (exceededLimitGuessCount(user, setting)) {
			// 추측가능 횟수에 도달하였으나 숫자를 못맞춘경우
			return 5.0f;
		} else if (exceededLimitWrongCount(user, setting)) {
			return 0.0f;
		}
		return 0;
	}

	private static boolean exceededLimitWrongCount(User user, Setting setting) {
		return user.getWrongCount() == setting.getLimitWrongInputCount();
	}

	private static long makeBasicDependerBaseScore(final GameRoom gameRoom) {
		return DEPENDER_BASE * getAttackerCount(gameRoom) - (getSolvedUserCount(gameRoom) * DEPENDER_EACH_USER);
	}

	private static long makeAllUserFocusedDependerBaseScore() {
		return 10L;
	}

	private static long makeSuccessAttackerBaseScore(final User user, final GameRoom gameRoom) {
		return ATTACKER_BASE * getAttackerCount(gameRoom) - ((user.getRank().getRanking() - 1) * ATTACKER_EACH_USER);
	}

	private static boolean successGuess(final User user) {
		return user.getResult().getSettlement().isSolved() && user.getRank() != null && user.getRank().getRanking()
				> 0;
	}

	private static boolean exceededLimitGuessCount(final User user, final Setting setting) {
		return setting.getLimitGuessInputCount() == user.getGuessCount();
	}

	private static int scoreCalculation(final User user, final Setting setting, final float baseScore) {
		final int guessInputCount = setting.getLimitGuessInputCount();
		final int generationNumberCount = setting.getGenerationNumberCount();

		float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, user);
		float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, user);

		return Math.round(guessScoreValue + numberCountScoreValue);
	}

	private static float getNumberCountScoreValue(final int generationNumberCount, final float baseScore, final User
			user) {
		float numberCountScoreValue;
		final boolean isAttacker = user.getRole().getRoleType().equals(RoleType.ATTACKER);
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

	private static float getGuessScoreValue(final int guessInputCount, final float baseScore, final User user) {
		float guessScoreValue;
		final boolean isAttacker = user.getRole().getRoleType().equals(RoleType.ATTACKER);
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