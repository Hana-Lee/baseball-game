package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Setting {
	//잘못된 숫자 입력 횟수 제한
	private int limitWrongInputCount = 5;

	// 야구 게임 횟수
	private int limitGuessInputCount = 10;

	// 생성 숫자 갯수
	private int generationNumberCount = 3;
}