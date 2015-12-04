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
			menus.add(new Menu(1, "시작", "게임 시작"));
			menus.add(new Menu(2, "점수", "게임 점수 리스트"));
			menus.add(new Menu(3, "설정", "게임 규칙 설정"));
			menus.add(new Menu(0, "종료", "게임 종료"));
		}
		return menus;
	}
}
