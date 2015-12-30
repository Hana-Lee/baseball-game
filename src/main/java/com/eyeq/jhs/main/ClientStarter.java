package com.eyeq.jhs.main;

import com.eyeq.jhs.controller.GameClient;

import java.io.IOException;

/**
 * @author Hana Lee
 * @since 2015-12-26 18:58
 */
public class ClientStarter {

	public static void main(String[] args) {
		GameClient client = new GameClient();
		try {
			client.startGame();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}