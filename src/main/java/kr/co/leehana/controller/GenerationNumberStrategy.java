package kr.co.leehana.controller;

import kr.co.leehana.model.Setting;

public interface GenerationNumberStrategy {

	String generateRandomNumber(final Setting setting);

	Integer generateRoomNumber();
}
