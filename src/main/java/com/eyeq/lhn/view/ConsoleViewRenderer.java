package com.eyeq.lhn.view;

import com.eyeq.lhn.model.GuessResult;
import com.eyeq.lhn.model.Menu;
import com.eyeq.lhn.model.Score;
import com.eyeq.lhn.setting.GameSetting;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-22 17:04
 */
public class ConsoleViewRenderer implements ViewRenderer {

	@Override
	public void renderTitle() {
		System.out.println("==== 야구 게임 v0.0.1 ====");
	}

	@Override
	public void renderWelcome() {
		System.out.println("야구게임에 오신것을 환영합니다");
		System.out.println("즐거운 게임 되시기 바랍니다 ^^");
	}

	@Override
	public void renderMenu(List<Menu> menus) {
		System.out.println("==== 게임 메뉴 ====");
		for (Menu menu : menus) {
			System.out.println(menu.getId() + "." + menu.getName() + " (" + menu.getDescription() + ")");
		}
		System.out.print("메뉴를 선택 해 주세요 : ");
	}

	@Override
	public void renderSettingMenu(GameSetting setting) {
		System.out.println("=== 게임 설정 ====");
		System.out.println("사용자 입력 제한 횟수 (" + setting.getUserInputCountLimit() + "회) : ");
		System.out.println("랜덤 숫자 생성 갯수 (" + setting.getGenerateNumberCount() + "개) : ");
	}

	@Override
	public void renderInputNumberMessage(GameSetting setting) {
		System.out.println("입력 가능한 숫자는 " + setting.getUserInputCountLimit() + "개 입니다.");
		System.out.println("0 - 9 사이의 숫자중 중복 되지 않게 숫자를 입력해주세요.");
		System.out.print("숫자를 입력해 주세요 : ");
	}

	@Override
	public void renderGuessResult(GuessResult result) {
		System.out.println("스트라이크: " + result.getStrike() + ", 볼: " + result.getBall() + " 입니다.");
	}

	@Override
	public void renderGameCount(int inputCount) {
		System.out.println((inputCount + 1) + " 번째 입력입니다.");
	}

	@Override
	public void renderGameEnd(GuessResult result, int inputCount) {
		System.out.println("게임이 종료 되었습니다.");
	}

	@Override
	public void renderScore(Score score) {
		System.out.println("게임 점수 : " + score.getScore() + "점");
	}

	@Override
	public void renderInputNameMessage() {
		System.out.print("점수 현황에 등록 될 이름을 입력 해주세요 : ");
	}

	@Override
	public void renderAllScores(List<Score> scoreList) {
		System.out.println("==== 게임 점수 리스트 ====");
		System.out.println("랭크\t이름\t점수");
		int rank = 1;
		for (Score score : scoreList) {
			System.out.println(rank++ + "\t" + score.getName() + "\t" + score.getScore());
		}
	}
}
