package com.eyeq.jhs.type;

import lombok.Getter;

/**
 * @author Hana Lee
 * @since 2015-12-24 23-23
 */
public enum ErrorType {

	OVER_INPUT("한번에 입력 가능한 횟수를 초과 하였습니다."), ONLY_NUMBER("숫자만 입력 가능합니다."), DUPLICATE_USER_ID("중복 아이디 입니다"),
	ALREADY_JOIN("이미 게임룸에 존재합니다");

	@Getter
	private String message;

	ErrorType(String message) {
		this.message = message;
	}
}