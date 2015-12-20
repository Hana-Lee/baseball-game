package com.eyeq.jhs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Setting {
	//잘못된 숫자 입력 횟수 제한
	private int limitInputWrongNum = 5;

	// 야구 게임 횟수
	private int numberOfInputNum = 10;
}