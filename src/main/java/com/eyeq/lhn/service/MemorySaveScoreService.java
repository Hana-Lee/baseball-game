package com.eyeq.lhn.service;

import com.eyeq.lhn.model.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-23 21:38
 */
public class MemorySaveScoreService implements ScoreService {

	private List<Score> scoreData;

	@Override
	public void save(List<Score> results) {
		scoreData = results;
	}

	@Override
	public List<Score> load() {
		if (scoreData == null) {
			scoreData = new ArrayList<>();
		}
		return scoreData;
	}

	@Override
	public void delete() {
		scoreData = null;
	}
}
