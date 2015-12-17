package com.eyeq.lhn.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Hana Lee
 * @since 2015-12-15 16-12
 */
public class ServerBackground {

	private ServerSocket serverSocket;
	private Socket socket;

	public static void main(String[] args) throws IOException {
		ServerBackground serverBackground = new ServerBackground();
		serverBackground.setting();
	}

	private void setting() throws IOException {
		serverSocket = new ServerSocket(8888);

//		while (true) {
			System.out.println("서버 대기중...");
			socket = serverSocket.accept();

			System.out.println(socket.getInetAddress() + " 에서 접속.");

			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			String clientMsg = dataInputStream.readUTF();
			System.out.println("Client Msg : " + clientMsg);

			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF("서버 입니다");
//			ServerReceiver serverReceiver = new ServerReceiver(socket);
//			serverReceiver.start();
//		}
	}

	private class ServerReceiver extends Thread {

		private DataInputStream dataInputStream;
		private DataOutputStream dataOutputStream;

		public ServerReceiver(Socket socket) {
			try {
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (dataInputStream != null) {
				try {
					String clientMsg = dataInputStream.readUTF();
					System.out.println("Client Msg : " + clientMsg);
					dataOutputStream.writeUTF("메세지를 잘 수신했습니다. : " + clientMsg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
