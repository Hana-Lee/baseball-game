package com.eyeq.jhs.controller;

import com.eyeq.jhs.factory.GameRoomMaker;
import com.eyeq.jhs.model.GameRoom;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;
import com.eyeq.jhs.strategy.RandomNumberGenerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerBackground {

	private ServerSocket server = null;
	private Socket client = null;

	private List<GameRoom> gameRoomList;

	public ServerBackground() {
		this.gameRoomList = GameRoomMaker.make();
	}

	public void startServer() {
		try {
			server = new ServerSocket(9090);
			while (true) {
				System.out.println("Server: Waiting for request.");
				client = server.accept();
				System.out.println("Server: accepted.");

				final GenerationNumberStrategy strategy = new RandomNumberGenerator();
				final ServerController receiver = new ServerController(new GameController(strategy), client,
						gameRoomList);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					// 소켓수신빌요없는 경우 서버소켓종료
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}