package kr.co.leehana;

import kr.co.leehana.controller.ScoreCalculator;
import kr.co.leehana.model.Ball;
import kr.co.leehana.model.GuessNumberComparedResult;
import kr.co.leehana.model.OldGameRoom;
import kr.co.leehana.model.OldUser;
import kr.co.leehana.model.Rank;
import kr.co.leehana.model.Role;
import kr.co.leehana.model.Score;
import kr.co.leehana.model.Setting;
import kr.co.leehana.model.Settlement;
import kr.co.leehana.model.Strike;
import kr.co.leehana.enums.GameRole;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Hana Lee
 * @since 2016-01-03 20:36
 */
public class ScoreCalculatorTest {

	private OldGameRoom gameRoom;

	@Before
	public void setUp() {
		this.gameRoom = new OldGameRoom();
		this.gameRoom.setName("루비");
	}

	@Test
	public void testDependerScoreCalculation() {
		// Very Hard Guess, Easy Generation
		int[][] expScores = new int[][]{
				{
					// 4명중 0명 맞춤
					391,
					// 4명중 1명 맞춤
					342,
					// 4명중 2명 맞춤
					293,
					// 4명중 3명 맞춤
					244
				},
				{
					// 3명중 0명 맞춤
					293,
					// 3명중 1명 맞춤
					244,
					// 3명중 2명 맞춤
					196
				},
				{
					// 2명중 0명 맞춤
					196,
					// 2명중 1명 맞춤
					147
				}
		};
		assertDependerScoreCalculationEachLevelCase(1, 2, expScores, "Very Hard Guess, Easy Generation, %d Users");
		// Hard Guess, Easy Generation
		expScores = new int[][]{{427, 373, 320, 267}, {320, 267, 213}, {213, 160}};
		assertDependerScoreCalculationEachLevelCase(5, 2, expScores, "Hard Guess, Easy Generation, %d Users");
		// Normal Guess, Easy Generation
		expScores = new int[][]{{480, 420, 360, 300}, {360, 300, 240}, {240, 180}};
		assertDependerScoreCalculationEachLevelCase(10, 2, expScores, "Normal Guess, Easy Generation, %d Users");
		// Easy Guess, Easy Generation
		expScores = new int[][]{{560, 490, 420, 350}, {420, 350, 280}, {280, 210}};
		assertDependerScoreCalculationEachLevelCase(15, 2, expScores, "Easy Guess, Easy Generation, %d Users");
		// Very Easy Guess, Easy Generation
		expScores = new int[][]{{680, 595, 510, 425}, {510, 425, 340}, {340, 255}};
		assertDependerScoreCalculationEachLevelCase(20, 2, expScores, "Very Easy Guess, Easy Generation, %d Users");

		// Very Hard Guess, Normal Generation
		expScores = new int[][]{{231, 202, 173, 144}, {173, 144, 116}, {116, 87}};
		assertDependerScoreCalculationEachLevelCase(1, 3, expScores, "Very Hard Guess, Normal Generation, %d Users");
		// Hard Guess, Normal Generation
		expScores = new int[][]{{267, 233, 200, 167}, {200, 167, 133}, {133, 100}};
		assertDependerScoreCalculationEachLevelCase(5, 3, expScores, "Hard Guess, Normal Generation, %d Users");
		// Normal Guess, Normal Generation
		expScores = new int[][]{{320, 280, 240, 200}, {240, 200, 160}, {160, 120}};
		assertDependerScoreCalculationEachLevelCase(10, 3, expScores, "Normal Guess, Normal Generation, %d Users");
		// Easy Guess, Normal Generation
		expScores = new int[][]{{400, 350, 300, 250}, {300, 250, 200}, {200, 150}};
		assertDependerScoreCalculationEachLevelCase(15, 3, expScores, "Easy Guess, Normal Generation, %d Users");
		// Very Easy Guess, Normal Generation
		expScores = new int[][]{{520, 455, 390, 325}, {390, 325, 260}, {260, 195}};
		assertDependerScoreCalculationEachLevelCase(20, 3, expScores, "Very Easy Guess, Normal Generation, %d Users");

		// Very Hard Guess, Hard Generation
		expScores = new int[][]{{151, 132, 113, 94}, {113, 94, 76}, {76, 57}};
		assertDependerScoreCalculationEachLevelCase(1, 4, expScores, "Very Hard Guess, Hard Generation, %d Users");
		// Hard Guess, Hard Generation
		expScores = new int[][]{{187, 163, 140, 117}, {140, 117, 93}, {93, 70}};
		assertDependerScoreCalculationEachLevelCase(5, 4, expScores, "Hard Guess, Hard Generation, %d Users");
		// Normal Guess, Hard Generation
		expScores = new int[][]{{240, 210, 180, 150}, {180, 150, 120}, {120, 90}};
		assertDependerScoreCalculationEachLevelCase(10, 4, expScores, "Normal Guess, Hard Generation, %d Users");
		// Easy Guess, Hard Generation
		expScores = new int[][]{{320, 280, 240, 200}, {240, 200, 160}, {160, 120}};
		assertDependerScoreCalculationEachLevelCase(15, 4, expScores, "Easy Guess, Hard Generation, %d Users");
		// Very Easy Guess, Hard Generation
		expScores = new int[][]{{440, 385, 330, 275}, {330, 275, 220}, {220, 165}};
		assertDependerScoreCalculationEachLevelCase(20, 4, expScores, "Very Easy Guess, Hard Generation, %d Users");

		// Very Hard Guess, Very Hard Generation
		expScores = new int[][]{{111, 97, 83, 69}, {83, 69, 56}, {56, 42}};
		assertDependerScoreCalculationEachLevelCase(1, 5, expScores, "Very Hard Guess, Very Hard Generation, %d Users");
		// Hard Guess, Very Hard Generation
		expScores = new int[][]{{147, 128, 110, 92}, {110, 92, 73}, {73, 55}};
		assertDependerScoreCalculationEachLevelCase(5, 5, expScores, "Hard Guess, Very Hard Generation, %d Users");
		// Normal Guess, Very Hard Generation
		expScores = new int[][]{{200, 175, 150, 125}, {150, 125, 100}, {100, 75}};
		assertDependerScoreCalculationEachLevelCase(10, 5, expScores, "Normal Guess, Very Hard Generation, %d Users");
		// Easy Guess, Very Hard Generation
		expScores = new int[][]{{280, 245, 210, 175}, {210, 175, 140}, {140, 105}};
		assertDependerScoreCalculationEachLevelCase(15, 5, expScores, "Easy Guess, Very Hard Generation, %d Users");
		// Very Easy Guess, Very Hard Generation
		expScores = new int[][]{{400, 350, 300, 250}, {300, 250, 200}, {200, 150}};
		assertDependerScoreCalculationEachLevelCase(20, 5, expScores, "Very Easy Guess, Very Hard Generation, %d Users");
	}

	private void assertDependerScoreCalculationEachLevelCase(int guessLevel, int generationLevel, int[][] expScoreArray,
	                                                         String assertFailMessage) {
		for (int[] expScores : expScoreArray) {
			assertDependerScoreCalculationEachLevelCase(guessLevel, generationLevel, expScores, assertFailMessage);
		}
	}

	private void assertDependerScoreCalculationEachLevelCase(int guessLevel, int generationLevel, int[] expScores,
	                                                         String assertFailMessage) {
		for (int solvedUserCount = 0; solvedUserCount < expScores.length; solvedUserCount++) {
			makeGameRoomForTest(guessLevel, generationLevel, expScores.length, solvedUserCount);
			Score score = ScoreCalculator.dependerScore(findDepender(), gameRoom);
			assertEquals(String.format(assertFailMessage, expScores.length), expScores[solvedUserCount], score.getValue());
		}
	}

	private void makeGameRoomForTest(int guessLevel, int generationLevel, int attackUserCount, int solvedUserCount) {
		this.gameRoom.getUsers().clear();
		this.gameRoom.setSetting(new Setting(5, guessLevel, generationLevel));

		OldUser user = new OldUser("이하나", new Role(GameRole.DEFENDER), new Score());
		this.gameRoom.getUsers().add(user);

		int count = 0;
		for (int i = 1; i <= attackUserCount; i++) {
			OldUser newUser = new OldUser("이하나" + i, new Role(GameRole.ATTACKER), new Score());
			if (count < solvedUserCount) {
				newUser.setResult(new GuessNumberComparedResult(new Settlement(true), new Strike(2), new Ball(0)));
			} else {
				newUser.setResult(new GuessNumberComparedResult(new Settlement(false), new Strike(2), new Ball(0)));
			}
			this.gameRoom.getUsers().add(newUser);

			count++;
		}
	}

	private OldUser findDepender() {
		return this.gameRoom.getUsers().stream().filter(u -> u.getRole().getRoleType().equals(GameRole.DEFENDER)).findFirst().get();
	}

	@Test
	public void testAllUserFocusedDependerScoreCalculation() {
		this.gameRoom.setSetting(new Setting());
		final OldUser depender = new OldUser("이하나", new Role(GameRole.DEFENDER), new Score());
		final OldUser attacker = new OldUser("이두나", new Role(GameRole.ATTACKER), new Score());
		attacker.setGameOver(true);
		attacker.setResult(new GuessNumberComparedResult(new Settlement(true), new Strike(3), new Ball(0)));

		this.gameRoom.getUsers().add(depender);
		this.gameRoom.getUsers().add(attacker);

		final Score dependerScore = ScoreCalculator.calculation(depender, this.gameRoom);

		assertEquals("기본 설정에서 모든 유저가 맞춘경우 수비자의 점수는 20점 이여야 합니다", 20, dependerScore.getValue());
	}

	@Test
	public void testAttackerScoreCalculation() {
		// Very Hard Guess, Easy Generation
		Integer[][] expScore = new Integer[][]{
				// 1명중 1등의 점수
				{55},
				// 2명중 1등의 점수, 2등의 점수
				{110, 83},
				// 3명중 1등의 점수, 2등의 점수, 3등의 점수
				{165, 138, 110},
				// 4명중 1등의 점수, 2등의 점수, 3등의 점수, 4등의 점수
				{220, 193, 165, 138},
				// 5명중 1등의 점수, 2등의 점수, 3등의 점수, 4등의 점수, 5등의 점수
				{275, 248, 220, 193, 165}};

		// Guess Level, Generation Level, Expected Scores
		assertAttackerScoreCalculationEachLevelCase(1, 2, expScore);
		// Hard Guess, Easy Generation
		expScore = new Integer[][]{{40}, {80, 60}, {120, 100, 80}, {160, 140, 120, 100}, {200, 180, 160, 140, 120}};
		assertAttackerScoreCalculationEachLevelCase(5, 2, expScore);
		// Normal Guess, Easy Generation
		expScore = new Integer[][]{{30}, {60, 45}, {90, 75, 60}, {120, 105, 90, 75}, {150, 135, 120, 105, 90}};
		assertAttackerScoreCalculationEachLevelCase(10, 2, expScore);
		// Easy Guess, Easy Generation
		expScore = new Integer[][]{{23}, {47, 35}, {70, 58, 47}, {93, 82, 70, 58}, {117, 105, 93, 82, 70}};
		assertAttackerScoreCalculationEachLevelCase(15, 2, expScore);
		// Very Easy Guess, Easy Generation
		expScore = new Integer[][]{{19}, {38, 28}, {57, 47, 38}, {76, 66, 57, 47}, {94, 85, 76, 66, 57}};
		assertAttackerScoreCalculationEachLevelCase(20, 2, expScore);

		// Very Hard Guess, Normal Generation
		expScore = new Integer[][]{{65}, {130, 98}, {195, 163, 130}, {260, 228, 195, 163}, {325, 293, 260, 228, 195}};
		assertAttackerScoreCalculationEachLevelCase(1, 3, expScore);
		// Hard Guess, Normal Generation
		expScore = new Integer[][]{{50}, {100, 75}, {150, 125, 100}, {200, 175, 150, 125}, {250, 225, 200, 175, 150}};
		assertAttackerScoreCalculationEachLevelCase(5, 3, expScore);
		// Normal Guess, Normal Generation
		expScore = new Integer[][]{{40}, {80, 60}, {120, 100, 80}, {160, 140, 120, 100}, {200, 180, 160, 140, 120}};
		assertAttackerScoreCalculationEachLevelCase(10, 3, expScore);
		// Easy Guess, Normal Generation
		expScore = new Integer[][]{{33}, {67, 50}, {100, 83, 67}, {133, 117, 100, 83}, {167, 150, 133, 117, 100}};
		assertAttackerScoreCalculationEachLevelCase(15, 3, expScore);
		// Very Easy Guess, Normal Generation
		expScore = new Integer[][]{{29}, {58, 43}, {87, 72, 58}, {116, 101, 87, 72}, {144, 130, 116, 101, 87}};
		assertAttackerScoreCalculationEachLevelCase(20, 3, expScore);

		// Very Hard Guess, Hard Generation
		expScore = new Integer[][]{{85}, {170, 128}, {255, 213, 170}, {340, 298, 255, 213}, {425, 383, 340, 298, 255}};
		assertAttackerScoreCalculationEachLevelCase(1, 4, expScore);
		// Hard Guess, Hard Generation
		expScore = new Integer[][]{{70}, {140, 105}, {210, 175, 140}, {280, 245, 210, 175}, {350, 315, 280, 245, 210}};
		assertAttackerScoreCalculationEachLevelCase(5, 4, expScore);
		// Normal Guess, Hard Generation
		expScore = new Integer[][]{{60}, {120, 90}, {180, 150, 120}, {240, 210, 180, 150}, {300, 270, 240, 210, 180}};
		assertAttackerScoreCalculationEachLevelCase(10, 4, expScore);
		// Easy Guess, Hard Generation
		expScore = new Integer[][]{{53}, {107, 80}, {160, 133, 107}, {213, 187, 160, 133}, {267, 240, 213, 187, 160}};
		assertAttackerScoreCalculationEachLevelCase(15, 4, expScore);
		// Very Easy Guess, Hard Generation
		expScore = new Integer[][]{{49}, {98, 73}, {147, 122, 98}, {196, 171, 147, 122}, {244, 220, 196, 171, 147}};
		assertAttackerScoreCalculationEachLevelCase(20, 4, expScore);

		// Very Hard Guess, Very Hard Generation
		expScore = new Integer[][]{{125}, {250, 188}, {375, 313, 250}, {500, 438, 375, 313}, {625, 563, 500, 438, 375}};
		assertAttackerScoreCalculationEachLevelCase(1, 5, expScore);
		// Hard Guess, Very Hard Generation
		expScore = new Integer[][]{{110}, {220, 165}, {330, 275, 220}, {440, 385, 330, 275}, {550, 495, 440, 385, 330}};
		assertAttackerScoreCalculationEachLevelCase(5, 5, expScore);
		// Normal Guess, Very Hard Generation
		expScore = new Integer[][]{{100}, {200, 150}, {300, 250, 200}, {400, 350, 300, 250}, {500, 450, 400, 350, 300}};
		assertAttackerScoreCalculationEachLevelCase(10, 5, expScore);
		// Easy Guess, Very Hard Generation
		expScore = new Integer[][]{{93}, {187, 140}, {280, 233, 187}, {373, 327, 280, 233}, {467, 420, 373, 327, 280}};
		assertAttackerScoreCalculationEachLevelCase(15, 5, expScore);
		// Very Easy Guess, Very Hard Generation
		expScore = new Integer[][]{{89}, {178, 133}, {267, 222, 178}, {356, 311, 267, 222}, {444, 400, 356, 311, 267}};
		assertAttackerScoreCalculationEachLevelCase(20, 5, expScore);
	}

	// 공격자 점수 계산 테스트 공통 사용 메소드
	private void assertAttackerScoreCalculationEachLevelCase(int guessLevel, int generationLevel, Integer[][] scores) {
		this.gameRoom.getUsers().clear();
		for (Integer[] score : scores) {
			final int idx = gameRoom.getUsers().size() + 1;
			assertAttackerScoreCalculation("이하나" + idx, new Setting(5, guessLevel, generationLevel), new Rank(idx), idx,
					Arrays.asList(score));
		}
	}

	// 공격자 점수 계산 테스트 공통 사용 메소드
	private void assertAttackerScoreCalculation(String userId, Setting setting, Rank rank, int userCount, List<Integer>
			expectedScores) {
		// 공동으로 사용할 result 인스턴스
		final GuessNumberComparedResult result = new GuessNumberComparedResult(new Settlement(true), new Strike(3), new Ball(0));
		this.gameRoom.setSetting(setting);
		// 총 1명
		final OldUser newUser = new OldUser(userId, new Role(GameRole.ATTACKER), new Score());
		newUser.setRank(rank);
		newUser.setResult(result);
		this.gameRoom.getUsers().add(newUser);

		assertEquals(userCount, gameRoom.getUsers().size());

		int count = 0;
		for (OldUser user : this.gameRoom.getUsers()) {
			final Score score = ScoreCalculator.attackerScore(user, gameRoom);
			final int expectedScore = expectedScores.get(count++);
			assertEquals(expectedScore, score.getValue());
		}
	}

	// 숫자를 맞추지 못한경우 점수 계산 테스트
	@Test
	public void testNotFocusedScoreCalculation() {
		// Very Hard Guess, Easy Generation, Expected Score
		assertNotFocusedScoreCalculation(1, 2, 14);
		// Hard Guess, Easy Generation, Expected Score
		assertNotFocusedScoreCalculation(5, 2, 10);
		// Normal Guess, Easy Generation, Expected Score
		assertNotFocusedScoreCalculation(10, 2, 8);
		// Easy Guess, Easy Generation, Expected Score
		assertNotFocusedScoreCalculation(15, 2, 6);
		// Very Easy Guess, Easy Generation, Expected Score
		assertNotFocusedScoreCalculation(20, 2, 5);

		// Very Hard Guess, Normal Generation, Expected Score
		assertNotFocusedScoreCalculation(1, 3, 16);
		// Hard Guess, Normal Generation, Expected Score
		assertNotFocusedScoreCalculation(5, 3, 13);
		// Normal Guess, Normal Generation, Expected Score
		assertNotFocusedScoreCalculation(10, 3, 10);
		// Easy Guess, Normal Generation, Expected Score
		assertNotFocusedScoreCalculation(15, 3, 8);
		// Very Easy Guess, Normal Generation, Expected Score
		assertNotFocusedScoreCalculation(20, 3, 7);

		// Very Hard Guess, Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(1, 4, 21);
		// Hard Guess, Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(5, 4, 18);
		// Normal Guess, Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(10, 4, 15);
		// Easy Guess, Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(15, 4, 13);
		// Very Easy Guess, Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(20, 4, 12);

		// Very Hard Guess, Very Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(1, 5, 31);
		// Hard Guess, Very Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(5, 5, 28);
		// Normal Guess, Very Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(10, 5, 25);
		// Easy Guess, Very Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(15, 5, 23);
		// Very Easy Guess, Very Hard Generation, Expected Score
		assertNotFocusedScoreCalculation(20, 5, 22);
	}

	private void assertNotFocusedScoreCalculation(int guessLevel, int generationLevel, int expectedScore) {
		final GuessNumberComparedResult result = new GuessNumberComparedResult(new Settlement(false), new Strike(2), new Ball(1));
		final Setting setting = new Setting(5, guessLevel, generationLevel);
		gameRoom.setSetting(setting);

		final OldUser user = new OldUser("이하나", new Role(GameRole.ATTACKER), new Score());
		user.setRank(new Rank(0));
		user.setGuessCount(guessLevel);
		user.setResult(result);
		this.gameRoom.getUsers().clear();
		this.gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		final Score score = ScoreCalculator.attackerScore(user, gameRoom);
		assertEquals(expectedScore, score.getValue());
	}

	// 잘못된 입력 초과시 점수 계산 테스트
	@Test
	public void testWrongNumberScoreCalculation() {
		final OldUser user = new OldUser("이하나", new Role(GameRole.ATTACKER), new Score());
		user.setResult(new GuessNumberComparedResult(new Settlement(false), new Strike(0), new Ball(0)));
		user.setGuessCount(0);
		user.setWrongCount(5);
		gameRoom.setSetting(new Setting());
		gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		final Score score = ScoreCalculator.attackerScore(user, gameRoom);

		assertEquals(0, score.getValue());
	}

	@Test
	public void testTotalScoreCalculation() {
		final OldUser user = new OldUser("이하나", new Role(GameRole.ATTACKER), new Score());
		user.setResult(new GuessNumberComparedResult(new Settlement(true), new Strike(3), new Ball(0)));
		user.setGuessCount(2);
		user.setRank(new Rank(1));

		gameRoom.setSetting(new Setting());
		gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		final Score firstScore = ScoreCalculator.calculation(user, gameRoom);
		user.getTotalScore().setValue(user.getTotalScore().getValue() + firstScore.getValue());
		assertEquals("기본 설정, 1명중 1등은 40점이여야 합니다.", 40, firstScore.getValue());

		final OldUser secUser = new OldUser("이두나", new Role(GameRole.ATTACKER), new Score());
		secUser.setRank(new Rank(1));
		gameRoom.getUsers().add(secUser);

		assertEquals(2, gameRoom.getUsers().size());

		user.setRank(new Rank(2));

		final Score secScore = ScoreCalculator.calculation(user, gameRoom);
		user.getTotalScore().setValue(user.getTotalScore().getValue() + secScore.getValue());
		assertEquals("기본 설정, 2명중 1등은 60점이여야 합니다.", 60, secScore.getValue());
		assertEquals("총점은 100점 이여야 합니다.", 100, user.getTotalScore().getValue());
	}
}
