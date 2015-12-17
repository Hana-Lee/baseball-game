package com.eyeq.jhs.model;

public class Setting {
	private int limitInputWrongNum;
	private int numberOfInputNum;
	
	public Setting() {
		//잘못된 숫자 입력 횟수 제한
		limitInputWrongNum = 5;
		// 야구 게임 횟수
		numberOfInputNum = 10;
	}
	
	
	public int getLimitInputWrongNum() {
		return limitInputWrongNum;
	}
	
	public void setLimitInputWrongNum(int totalGameNumber) {
		this.limitInputWrongNum = totalGameNumber;
	}
	public int getNumberOfInputNum() {
		return numberOfInputNum;
	}
	public void setNumberOfInputNum(int limitInputNumber) {
		this.numberOfInputNum = limitInputNumber;
	}
	

}

