package kr.co.leehana.controller;

import kr.co.leehana.model.OldGameRoom;
import kr.co.leehana.model.OldUser;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.Setting;
import kr.co.leehana.type.GameRole;

public class ScoreCalculator {

	private static final int DEPENDER_BASE = 40;
	private static final int DEPENDER_EACH_USER = 20;
	private static final int ATTACKER_BASE = 20;
	private static final int ATTACKER_EACH_USER = 10;

	public ScoreCalculator() {
	}

	public static Score calculation(final OldUser user, final OldGameRoom gameRoom) {
		if (user.getRole().getRoleType().equals(GameRole.ATTACKER)) {
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
	public static Score dependerScore(final OldUser user, final OldGameRoom gameRoom) {
		// TODO 수비자의 점수 계산이 잘못되는것 수정(모든 유저가 맞춘경우의 점수가 제대로 반영되지 않음)
		final float baseScore = makeBaseScore(user, gameRoom, gameRoom.getSetting());

		return new Score(scoreCalculation(user, gameRoom.getSetting(), baseScore));
	}

	private static long getSolvedUserCount(final OldGameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole.ATTACKER) && u
				.getResult().getSettlement().isSolved()).count();
	}

	private static long getAttackerCount(final OldGameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole.ATTACKER)).count();
	}

	/**
	 * 유저의 게임이 종료된 후 각각의 유저별로 계산 된다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param user     게임 유저
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	public static Score attackerScore(final OldUser user, final OldGameRoom gameRoom) {
		int totalScore = 0;

		if (user.getResult() != null) {
			final Setting setting = gameRoom.getSetting();

			final float baseScore = makeBaseScore(user, gameRoom, setting);

			totalScore = scoreCalculation(user, setting, baseScore);
		}

		return new Score(totalScore);
	}

	private static float makeBaseScore(final OldUser user, final OldGameRoom gameRoom, final Setting setting) {
		if (user.getRole().getRoleType().equals(GameRole.ATTACKER)) {
			return makeAttackerBaseScore(user, gameRoom, setting);
		} else if (user.getRole().getRoleType().equals(GameRole.DEFENDER)) {
			return makeDependerBaseScore(gameRoom);
		}

		return 0.0f;
	}

	private static float makeDependerBaseScore(final OldGameRoom gameRoom) {
		if (allUserFocusedNumber(gameRoom)) {
			return makeAllUserFocusedDependerBaseScore();
		} else {
			return makeBasicDependerBaseScore(gameRoom);
		}
	}

	private static boolean allUserFocusedNumber(final OldGameRoom gameRoom) {
		return gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole.ATTACKER) && u
				.getResult().getSettlement().isSolved()).count() == gameRoom.getUsers().stream().filter(u -> u.getRole
				().getRoleType().equals(GameRole.ATTACKER)).count();
	}

	private static float makeAttackerBaseScore(final OldUser user, final OldGameRoom gameRoom, final Setting setting) {
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

	private static boolean exceededLimitWrongCount(OldUser user, Setting setting) {
		return user.getWrongCount() == setting.getLimitWrongInputCount();
	}

	private static long makeBasicDependerBaseScore(final OldGameRoom gameRoom) {
		return DEPENDER_BASE * getAttackerCount(gameRoom) - (getSolvedUserCount(gameRoom) * DEPENDER_EACH_USER);
	}

	private static long makeAllUserFocusedDependerBaseScore() {
		return 10L;
	}

	private static long makeSuccessAttackerBaseScore(final OldUser user, final OldGameRoom gameRoom) {
		return ATTACKER_BASE * getAttackerCount(gameRoom) - ((user.getRank().getValue() - 1) * ATTACKER_EACH_USER);
	}

	private static boolean successGuess(final OldUser user) {
		return user.getResult().getSettlement().isSolved() && user.getRank() != null && user.getRank().getValue()
				> 0;
	}

	private static boolean exceededLimitGuessCount(final OldUser user, final Setting setting) {
		return setting.getLimitGuessInputCount() == user.getGuessCount();
	}

	private static int scoreCalculation(final OldUser user, final Setting setting, final float baseScore) {
		final int guessInputCount = setting.getLimitGuessInputCount();
		final int generationNumberCount = setting.getGenerationNumberCount();

		float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, user);
		float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, user);

		return Math.round(guessScoreValue + numberCountScoreValue);
	}

	private static float getNumberCountScoreValue(final int generationNumberCount, final float baseScore, final OldUser
			user) {
		float numberCountScoreValue;
		final boolean isAttacker = user.getRole().getRoleType().equals(GameRole.ATTACKER);
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

	private static float getGuessScoreValue(final int guessInputCount, final float baseScore, final OldUser user) {
		float guessScoreValue;
		final boolean isAttacker = user.getRole().getRoleType().equals(GameRole.ATTACKER);
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