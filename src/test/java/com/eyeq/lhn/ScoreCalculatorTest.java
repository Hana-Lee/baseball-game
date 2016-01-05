package com.eyeq.lhn;

import com.eyeq.jhs.model.Ball;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.model.Rank;
import com.eyeq.jhs.model.Result;
import com.eyeq.jhs.model.Role;
import com.eyeq.jhs.model.Score;
import com.eyeq.jhs.model.ScoreCalculator;
import com.eyeq.jhs.model.Setting;
import com.eyeq.jhs.model.Solve;
import com.eyeq.jhs.model.Strike;
import com.eyeq.jhs.model.User;
import com.eyeq.jhs.type.RoleType;
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

	private GameRoom gameRoom;

	@Before
	public void setUp() {
		this.gameRoom = new GameRoom();
		this.gameRoom.setName("루비");
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
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		this.gameRoom.setSetting(setting);
		// 총 1명
		final User newUser = new User(userId, new Role(RoleType.ATTACKER), true);
		newUser.setRank(rank);
		this.gameRoom.getUsers().add(newUser);

		assertEquals(userCount, gameRoom.getUsers().size());

		int count = 0;
		for (User user : this.gameRoom.getUsers()) {
			final Score score = ScoreCalculator.calculateScore(result, user, gameRoom);
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
		final Result result = new Result(new Solve(false), new Strike(2), new Ball(1));
		final Setting setting = new Setting(5, guessLevel, generationLevel);
		gameRoom.setSetting(setting);

		final User user = new User("이하나", new Role(RoleType.ATTACKER), true);
		user.setRank(new Rank(0));
		user.setGuessCount(guessLevel);
		this.gameRoom.getUsers().clear();
		this.gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		final Score score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(expectedScore, score.getValue());
	}

	// 잘못된 입력 초과시 점수 계산 테스트
	@Test
	public void testWrongNumberScoreCalculation() {
		final User user = new User("이하나", new Role(RoleType.ATTACKER), true);
		gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		final Score score = ScoreCalculator.calculateScore(null, user, gameRoom);

		assertEquals(0, score.getValue());
	}
}
