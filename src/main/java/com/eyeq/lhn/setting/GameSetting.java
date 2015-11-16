package com.eyeq.lhn.setting;

/**
 * @author Hana Lee
 * @since 2015-11-15 19:22
 */
public class GameSetting {

	private int userInputCountLimit;
	private int generateNumberCount;

	public void setUserInputCountLimit(int userInputCountLimit) {
		this.userInputCountLimit = userInputCountLimit;
	}

	public int getUserInputCountLimit() {
		return userInputCountLimit;
	}

	public void setGenerateNumberCount(int generateNumberCount) {
		this.generateNumberCount = generateNumberCount;
	}

	public int getGenerateNumberCount() {
		return generateNumberCount;
	}
}
