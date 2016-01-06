package kr.co.leehana.main;

import kr.co.leehana.controller.ServerBackground;

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
