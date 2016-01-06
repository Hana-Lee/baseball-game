package kr.co.leehana.controller;

import kr.co.leehana.model.Setting;

import java.util.LinkedHashSet;
import java.util.Set;

public class RandomNumberGenerator implements GenerationNumberStrategy {

	@Override
	public String generate(final Setting setting) {
		Set<String> randomNumbers = new LinkedHashSet<>();

		while (randomNumbers.size() < setting.getGenerationNumberCount()) {
			int rNumber = (int) (Math.random() * 10);
			randomNumbers.add(String.valueOf(rNumber));
		}
		return String.join("", randomNumbers);
	}
}