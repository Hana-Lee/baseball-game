package kr.co.leehana.util;

import kr.co.leehana.enums.GameRole;
import kr.co.leehana.model.GameRoom;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.Setting;

import java.util.Objects;

public class ScoreCalculator {

	private static final int DEPENDER_BASE = 40;
	private static final int DEPENDER_EACH_USER = 20;
	private static final int ATTACKER_BASE = 20;
	private static final int ATTACKER_EACH_USER = 10;

	public ScoreCalculator() {
	}

	public static Score calculation(final Player player, final GameRoom gameRoom) {
		if (Objects.equals(player.getGameRole(), GameRole.ATTACKER)) {
			return attackerScore(player, gameRoom);
		} else {
			return dependerScore(player, gameRoom);
		}
	}

	/**
	 * 게임이 종료된 후 수비자의 점수를 계산한다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param player   게임 플레이어
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	private static Score dependerScore(final Player player, final GameRoom gameRoom) {
		// TODO 수비자의 점수 계산이 잘못되는것 수정(모든 플레이어가 맞춘경우의 점수가 제대로 반영되지 않음)
		final float baseScore = makeBaseScore(player, gameRoom, gameRoom.getSetting());

		return new Score(scoreCalculation(player, gameRoom.getSetting(), baseScore));
	}

	private static long getSolvedPlayerCount(final GameRoom gameRoom) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getGameRole(), GameRole.ATTACKER) && p
				.getResult().getSettlement().getSolved()).count();
	}

	private static long getAttackerCount(final GameRoom gameRoom) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getGameRole(), GameRole.ATTACKER)).count();
	}

	/**
	 * 플레이어의 게임이 종료된 후 각각의 플레이어별로 계산 된다.
	 * 점수는 소숫점 반올림 적용
	 *
	 * @param player   게임 플레이어
	 * @param gameRoom 게임룸
	 * @return Score 계산된 점수 객체
	 */
	private static Score attackerScore(final Player player, final GameRoom gameRoom) {
		int totalScore = 0;

		if (player.getResult() != null) {
			final Setting setting = gameRoom.getSetting();

			final float baseScore = makeBaseScore(player, gameRoom, setting);

			totalScore = scoreCalculation(player, setting, baseScore);
		}

		return new Score(totalScore);
	}

	private static float makeBaseScore(final Player player, final GameRoom gameRoom, final Setting setting) {
		if (Objects.equals(player.getGameRole(), GameRole.ATTACKER)) {
			return makeAttackerBaseScore(player, gameRoom, setting);
		} else if (Objects.equals(player.getGameRole(), GameRole.DEFENDER)) {
			return makeDependerBaseScore(gameRoom);
		}

		return 0.0f;
	}

	private static float makeDependerBaseScore(final GameRoom gameRoom) {
		if (allPlayerFocusedNumber(gameRoom)) {
			return makeAllPlayerFocusedDependerBaseScore();
		} else {
			return makeBasicDependerBaseScore(gameRoom);
		}
	}

	private static boolean allPlayerFocusedNumber(final GameRoom gameRoom) {
		return gameRoom.getPlayers().stream().filter(p -> Objects.equals(p.getGameRole(), GameRole.ATTACKER) && p
				.getResult().getSettlement().getSolved()).count() == gameRoom.getPlayers().stream().filter(p ->
				Objects.equals(p.getGameRole(), GameRole.ATTACKER)).count();
	}

	private static float makeAttackerBaseScore(final Player player, final GameRoom gameRoom, final Setting setting) {
		if (successGuess(player)) {
			// 성공적으로 숫자를 맞춘경우 랭크가 0 이상
			return makeSuccessAttackerBaseScore(player, gameRoom);
		} else if (exceededLimitGuessCount(player, setting)) {
			// 추측가능 횟수에 도달하였으나 숫자를 못맞춘경우
			return 5.0f;
		} else if (exceededLimitWrongCount(player, setting)) {
			return 0.0f;
		}
		return 0;
	}

	private static boolean exceededLimitWrongCount(Player player, Setting setting) {
		return Objects.equals(player.getWrongCount(), setting.getLimitWrongInputCount());
	}

	private static long makeBasicDependerBaseScore(final GameRoom gameRoom) {
		return DEPENDER_BASE * getAttackerCount(gameRoom) - (getSolvedPlayerCount(gameRoom) * DEPENDER_EACH_USER);
	}

	private static long makeAllPlayerFocusedDependerBaseScore() {
		return 10L;
	}

	private static long makeSuccessAttackerBaseScore(final Player player, final GameRoom gameRoom) {
		return ATTACKER_BASE * getAttackerCount(gameRoom) - ((player.getRank().getValue() - 1) * ATTACKER_EACH_USER);
	}

	private static boolean successGuess(final Player player) {
		return player.getResult().getSettlement().getSolved() && player.getRank() != null && player.getRank().getValue
				() > 0;
	}

	private static boolean exceededLimitGuessCount(final Player player, final Setting setting) {
		return Objects.equals(setting.getLimitGuessInputCount(), player.getInputCount());
	}

	private static int scoreCalculation(final Player player, final Setting setting, final float baseScore) {
		final int guessInputCount = setting.getLimitGuessInputCount();
		final int generationNumberCount = setting.getGenerationNumberCount();

		float guessScoreValue = getGuessScoreValue(guessInputCount, baseScore, player);
		float numberCountScoreValue = getNumberCountScoreValue(generationNumberCount, baseScore, player);

		return Math.round(guessScoreValue + numberCountScoreValue);
	}

	private static float getNumberCountScoreValue(final int generationNumberCount, final float baseScore, final Player
			player) {
		float numberCountScoreValue;
		final boolean isAttacker = Objects.equals(player.getGameRole(), GameRole.ATTACKER);
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

	private static float getGuessScoreValue(final int guessInputCount, final float baseScore, final Player player) {
		float guessScoreValue;
		final boolean isAttacker = Objects.equals(player.getGameRole(), GameRole.ATTACKER);
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