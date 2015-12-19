package com.eyeq.jhs;

import com.eyeq.jhs.model.Setting;
import com.eyeq.jhs.strategy.GenerationNumberStrategy;
import com.eyeq.jhs.strategy.RandomNumberGenerator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BaseballGameServer {
	private ServerSocket server = null;
	private Socket client = null;

	public static void main(String[] args) {
		BaseballGameServer server = new BaseballGameServer();
		server.startServer();
	}

	public void startServer() {
		try {
			server = new ServerSocket(9999);
			while (true) {
				System.out.println("Server: Waiting for request.");
				client = server.accept();
				System.out.println("Server: accepted.");

				final Setting setting = new Setting();
				final GenerationNumberStrategy strategy = new RandomNumberGenerator();
				final Receiver receiver = new Receiver(new BaseballGameEngine(strategy, setting), client);
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