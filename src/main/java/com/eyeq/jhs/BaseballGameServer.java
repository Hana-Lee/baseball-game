package com.eyeq.jhs;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BaseballGameServer {
	ServerSocket server = null;
	Socket client = null;

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
				DataInputStream stream = new DataInputStream(client.getInputStream());

				String receivedData = stream.readUTF();

				System.out.println("Received data: " + receivedData);

				stream.close();
				// 데이터 처리 끝난 후 소켓종료
				client.close();

				System.out.println("-------------------------");
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
