package com.eyeq.jhs.type;

/**
 * @author Hana Lee
 * @since 2015-12-19 20:28
 */
public enum MessageType {

	START("start"), GUESS_NUM("guessNum"), RESULT("result"), BALL("ball"), STRIKE("strike"), GAMEOVER("gameover"),
	SCORE("score"), RESOLVED("resolved"), GET_SCORE("getScore");

	private String value;

	MessageType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
