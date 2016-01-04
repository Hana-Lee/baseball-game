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
		this.gameRoom.setSetting(new Setting());
		this.gameRoom.setLimit(5);
		this.gameRoom.setGenerationNumbers("123");
		this.gameRoom.setGameCount(0);
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 3개 10회)
	@Test
	public void testBasicSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(40, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(60, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(80, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(80, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(180, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(200, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 3개 5회)
	@Test
	public void testHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(50, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(75, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(125, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(125, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(175, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(200, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(175, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(200, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(225, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(250, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 3개 1회)
	@Test
	public void testVeryHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(1);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(65, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(98, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(130, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(130, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(163, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(195, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(163, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(195, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(228, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(260, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(195, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(228, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(260, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(293, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(325, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 3개 15회)
	@Test
	public void testEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(15);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(33, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(50, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(67, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(67, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(83, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(83, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(117, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(133, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(117, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(133, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(167, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 3개 20회)
	@Test
	public void testVeryEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(20);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(29, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(43, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(58, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(58, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(72, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(87, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(72, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(87, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(101, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(116, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(87, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(101, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(116, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(130, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(144, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 2개 10회)
	@Test
	public void testEasyGenerationCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(30, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(45, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(60, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(60, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(75, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(90, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(75, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(90, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(105, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(90, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(105, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(135, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(150, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 4개 10회)
	@Test
	public void testHardGenerationCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(60, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(90, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(180, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(180, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(210, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(240, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(180, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(210, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(240, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(270, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(300, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 5개 10회)
	@Test
	public void testVeryHardGenerationCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(150, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(200, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(200, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(250, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(300, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(250, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(300, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(350, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(400, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(300, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(350, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(400, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(450, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(500, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 2개 15회)
	@Test
	public void testEasyGenerationCountEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(23, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(35, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(47, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(47, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(58, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(70, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(58, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(70, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(82, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(93, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(70, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(82, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(93, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(105, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(117, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 2개 20회)
	@Test
	public void testEasyGenerationCountVeryEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(19, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(28, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(38, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(38, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(47, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(57, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(47, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(57, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(66, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(76, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(57, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(66, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(76, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(85, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(94, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 2개 5회)
	@Test
	public void testEasyGenerationCountHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(40, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(60, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(80, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(80, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(100, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(120, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(180, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(200, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 2개 1회)
	@Test
	public void testEasyGenerationCountVeryHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(55, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(83, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(110, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(110, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(138, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(165, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(138, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(165, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(193, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(220, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(165, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(193, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(220, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(248, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(275, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 4개 1회)
	@Test
	public void testHardGenerationCountVeryHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(85, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(128, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(170, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(170, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(213, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(255, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(213, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(255, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(298, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(340, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(255, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(298, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(340, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(383, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(425, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 4개 5회)
	// TODO 테스트 코드 완성하기
	@Test
	public void testHardGenerationCountHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(70, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(105, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(175, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(210, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(175, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(210, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(245, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(280, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(210, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(245, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(280, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(315, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(350, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 4개 15회)
	@Test
	public void testHardGenerationCountEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(53, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(80, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(107, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(107, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(133, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(133, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(187, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(213, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(160, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(187, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(213, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(240, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(267, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 4개 20회)
	@Test
	public void testHardGenerationCountVeryEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(49, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(73, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(98, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(98, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(122, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(147, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(122, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(147, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(171, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(196, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(147, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(171, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(196, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(220, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(244, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 5개 1회)
	@Test
	public void testVeryHardGenerationCountVeryHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(125, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(188, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(250, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(250, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(313, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(375, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(313, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(375, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(438, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(500, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(375, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(438, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(500, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(563, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(625, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 5개 5회)
	@Test
	public void testVeryHardGenerationCountHardGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(110, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(165, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(220, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(220, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(275, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(330, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(275, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(330, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(385, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(440, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(330, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(385, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(440, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(495, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(550, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 5개 15회)
	@Test
	public void testVeryHardGenerationCountEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(93, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(140, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(187, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(187, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(233, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(280, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(233, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(280, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(327, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(373, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(280, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(327, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(373, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(420, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(467, score.getValue());
	}

	// 공격자 점수 계산 규칙 테스트 (게임 설정 : 5개 20회)
	@Test
	public void testVeryHardGenerationCountVeryEasyGuessCountSettingScoreCalculation() {
		// 공동으로 사용할 result 인스턴스
		final Result result = new Result(new Solve(true), new Strike(3), new Ball(0));
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		// 총 1명
		final User firstRankUser = new User("이하나", new Role(RoleType.ATTACKER), true);
		firstRankUser.setRank(new Rank(1));
		this.gameRoom.getUsers().add(firstRankUser);

		assertEquals(1, gameRoom.getUsers().size());

		// 1명중 1등일때 점수 계산
		Score score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(89, score.getValue());

		// 총 2명
		final User secRankUser = new User("이두나", new Role(RoleType.ATTACKER), true);
		secRankUser.setRank(new Rank(2));
		this.gameRoom.getUsers().add(secRankUser);

		assertEquals(2, gameRoom.getUsers().size());

		// 2명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(133, score.getValue());

		// 2명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(178, score.getValue());

		// 총 3명
		final User thrRankUser = new User("이세나", new Role(RoleType.ATTACKER), true);
		thrRankUser.setRank(new Rank(3));
		this.gameRoom.getUsers().add(thrRankUser);

		assertEquals(3, gameRoom.getUsers().size());

		// 3명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(178, score.getValue());

		// 3명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(222, score.getValue());

		// 3명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(267, score.getValue());

		// 총 4명
		final User fourRankUser = new User("이네나", new Role(RoleType.ATTACKER), true);
		fourRankUser.setRank(new Rank(4));
		this.gameRoom.getUsers().add(fourRankUser);

		assertEquals(4, gameRoom.getUsers().size());

		// 4명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(222, score.getValue());

		// 4명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(267, score.getValue());

		// 4명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(311, score.getValue());

		// 4명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(356, score.getValue());

		// 총 5명
		final User fiveRankUser = new User("이다섯나", new Role(RoleType.ATTACKER), true);
		fiveRankUser.setRank(new Rank(5));
		this.gameRoom.getUsers().add(fiveRankUser);

		assertEquals(5, gameRoom.getUsers().size());

		// 5명중 5등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fiveRankUser, gameRoom);
		assertEquals(267, score.getValue());

		// 5명중 4등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, fourRankUser, gameRoom);
		assertEquals(311, score.getValue());

		// 5명중 3등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, thrRankUser, gameRoom);
		assertEquals(356, score.getValue());

		// 5명중 2등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, secRankUser, gameRoom);
		assertEquals(400, score.getValue());

		// 5명중 1등일때 점수 계산
		score = ScoreCalculator.calculateScore(result, firstRankUser, gameRoom);
		assertEquals(444, score.getValue());
	}

	// 숫자를 맞추지 못한경우 점수 계산 테스트
	@Test
	public void testNotFocused() {
		final Result result = new Result(new Solve(false), new Strike(2), new Ball(1));
		// 기본 설정
		final Setting setting = new Setting();
		setting.setLimitGuessInputCount(10);
		setting.setGenerationNumberCount(3);
		gameRoom.setSetting(setting);

		// 총 1명
		final User user = new User("이하나", new Role(RoleType.ATTACKER), true);
		user.setRank(new Rank(0));
		user.setGuessCount(10);
		this.gameRoom.getUsers().add(user);

		assertEquals(1, gameRoom.getUsers().size());

		Score score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(10, score.getValue());

		// Easy Guess, Normal Generation Number
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(3);
		gameRoom.setSetting(setting);

		user.setGuessCount(15);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(8, score.getValue());

		// Very Easy Guess, Normal Generation Number
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(3);
		gameRoom.setSetting(setting);

		user.setGuessCount(20);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(7, score.getValue());

		// Hard Guess, Normal Generation Number
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(3);
		gameRoom.setSetting(setting);

		user.setGuessCount(5);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(13, score.getValue());

		// Very Hard Guess, Normal Generation Number
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(3);
		gameRoom.setSetting(setting);

		user.setGuessCount(1);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(16, score.getValue());

		// Very Hard Guess, Easy Generation Number
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		user.setGuessCount(1);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(14, score.getValue());

		// Hard Guess, Easy Generation Number
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		user.setGuessCount(5);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(10, score.getValue());

		// Normal Guess, Easy Generation Number
		setting.setLimitGuessInputCount(10);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		user.setGuessCount(10);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(8, score.getValue());

		// Easy Guess, Easy Generation Number
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		user.setGuessCount(15);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(6, score.getValue());

		// Very Easy Guess, Easy Generation Number
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(2);
		gameRoom.setSetting(setting);

		user.setGuessCount(20);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(5, score.getValue());

		// Very Hard Guess, Hard Generation Number
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		user.setGuessCount(1);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(21, score.getValue());

		// Hard Guess, Hard Generation Number
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		user.setGuessCount(5);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(18, score.getValue());

		// Normal Guess, Hard Generation Number
		setting.setLimitGuessInputCount(10);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		user.setGuessCount(10);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(15, score.getValue());

		// Easy Guess, Hard Generation Number
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		user.setGuessCount(15);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(13, score.getValue());

		// Very Easy Guess, Hard Generation Number
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(4);
		gameRoom.setSetting(setting);

		user.setGuessCount(20);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(12, score.getValue());

		// Very Hard Guess, Very Hard Generation Number
		setting.setLimitGuessInputCount(1);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		user.setGuessCount(1);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(31, score.getValue());

		// Hard Guess, Very Hard Generation Number
		setting.setLimitGuessInputCount(5);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		user.setGuessCount(5);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(28, score.getValue());

		// Normal Guess, Very Hard Generation Number
		setting.setLimitGuessInputCount(10);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		user.setGuessCount(10);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(25, score.getValue());

		// Easy Guess, Very Hard Generation Number
		setting.setLimitGuessInputCount(15);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		user.setGuessCount(15);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(23, score.getValue());

		// Very Easy Guess, Very Hard Generation Number
		setting.setLimitGuessInputCount(20);
		setting.setGenerationNumberCount(5);
		gameRoom.setSetting(setting);

		user.setGuessCount(20);

		score = ScoreCalculator.calculateScore(result, user, gameRoom);
		assertEquals(22, score.getValue());
	}
}
