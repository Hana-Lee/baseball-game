package com.eyeq.lhn.controller;

import com.eyeq.lhn.setting.GameSetting;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Hana Lee
 * @since 2015-11-16 23-06
 */
public class GameNumberRandomGenerator implements GameNumberGenerator {

	private GameSetting gameSetting;

	public GameNumberRandomGenerator(GameSetting gameSetting) {
		this.gameSetting = gameSetting;
	}

	@Override
	public String generate() {
		Set<String> randomNumbers = new LinkedHashSet<>(3);

		while (randomNumbers.size() < gameSetting.getGenerateNumberCount()) {
			int rNumber = (int) (Math.random() * 10);
			randomNumbers.add(String.valueOf(rNumber));
		}
		return String.join("", randomNumbers);
	}
}
