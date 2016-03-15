package kr.co.leehana.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class GuessNumberComparedResult {

	private Settlement settlement;
	private Strike strike;
	private Ball ball;
}
