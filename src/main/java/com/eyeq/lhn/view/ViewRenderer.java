package com.eyeq.lhn.view;

import com.eyeq.lhn.model.GuessResult;
import com.eyeq.lhn.model.Menu;
import com.eyeq.lhn.model.Score;
import com.eyeq.lhn.setting.GameSetting;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-22 16:57
 */
public interface ViewRenderer {

	void renderTitle();

	void renderWelcome();

	void renderMenu(List<Menu> menus);

	void renderSettingMenu(GameSetting setting);

	void renderInputNumberMessage(GameSetting setting);

	void renderGuessResult(GuessResult result);

	void renderGameCount(int inputCount);

	void renderGameEnd(GuessResult result, int inputCount);

	void renderScore(Score score);

	void renderInputNameMessage();

	void renderAllScores(List<Score> scoreList);
}
