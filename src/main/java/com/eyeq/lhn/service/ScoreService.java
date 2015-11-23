package com.eyeq.lhn.service;

import com.eyeq.lhn.model.Score;

import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-15 20:16
 */
public interface ScoreService {
	void save(List<Score> results);

	List<Score> load();

	void delete();
}
