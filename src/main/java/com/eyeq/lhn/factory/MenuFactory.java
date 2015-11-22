package com.eyeq.lhn.factory;

import com.eyeq.lhn.model.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-22 17:25
 */
public class MenuFactory {

	private static List<Menu> menus;

	public static List<Menu> create() {
		if (menus == null) {
			menus = new ArrayList<>();
			menus.add(new Menu(1, "시작", "게임을 시작합니다."));
			menus.add(new Menu(2, "점수", "게임 점수 리스트를 봅니다"));
			menus.add(new Menu(3, "설정", "게임의 규칙을 설정 합니다."));
		}
		return menus;
	}
}
