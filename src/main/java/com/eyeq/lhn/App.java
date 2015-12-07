package com.eyeq.lhn;

import com.eyeq.lhn.controller.GameNumberRandomGenerator;
import com.eyeq.lhn.service.MemorySaveScoreService;
import com.eyeq.lhn.setting.GameSetting;
import com.eyeq.lhn.view.ConsoleViewRenderer;

/**
 * @author Hana Lee
 * @since 2015-12-07 17:55
 */
public class App {

	public static void main(String[] args) {
		BaseballGame baseballGame = new BaseballGame(new MemorySaveScoreService(), new ConsoleViewRenderer());
		GameSetting setting = new GameSetting();
		setting.setGenerateNumberCount(3);
		setting.setUserInputCountLimit(10);
		baseballGame.setGameNumberGenerator(new GameNumberRandomGenerator(setting));
		baseballGame.setSetting(setting);

		baseballGame.start();
	}
}
