package com.eyeq.jhs.main;

import com.eyeq.jhs.controller.ServerBackground;

/**
 * @author Hana Lee
 * @since 2015-12-26 18:58
 */
public class ServerStarter {

	public static void main(String[] args) {
		ServerBackground server = new ServerBackground();
		server.startServer();
	}
}
