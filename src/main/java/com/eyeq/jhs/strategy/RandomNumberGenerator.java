package com.eyeq.jhs.strategy;

public class RandomNumberGenerator implements GenerationNumberStrategy {

	@Override
	public String generateNumber() {
		boolean checkResult = false;
		int firstNum = 0, secondNum = 0, thirdNum = 0;

		while (!checkResult) {
			firstNum = (int) (Math.random() * 10);
			secondNum = (int) (Math.random() * 10);
			thirdNum = (int) (Math.random() * 10);

			if (firstNum == secondNum || secondNum == thirdNum || firstNum == thirdNum) {
				checkResult = false;
			} else {
				checkResult = true;
			}
		}


		return "" + firstNum + secondNum + thirdNum;
	}

}
