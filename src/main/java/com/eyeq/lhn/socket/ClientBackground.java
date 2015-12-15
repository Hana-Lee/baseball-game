package com.eyeq.lhn.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Hana Lee
 * @since 2015-12-15 16-12
 */
public class ClientBackground {

	private Socket socket;
	private DataOutputStream dataOutputStream;

	public static void main(String[] args) throws IOException {
		ClientBackground clientBackground = new ClientBackground();
		clientBackground.connect();

		while (true) {
			System.out.print("메세지를 입력해주세요 : ");
			Scanner s = new Scanner(System.in);
			if (s.hasNextLine()) {
				clientBackground.sendMessage(s.nextLine());
			}
		}
	}

	private void connect() throws IOException {
		socket = new Socket("127.0.0.1", 8888);
		System.out.println("서버 연결 완료.");
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		ClientReceiver clientReceiver = new ClientReceiver(socket);
		clientReceiver.start();
	}

	private class ClientReceiver extends Thread {

		private DataInputStream dataInputStream;
		private DataOutputStream dataOutputStream;

		public ClientReceiver(Socket socket) {
			try {
				dataInputStream = new DataInputStream(socket.getInputStream());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (dataInputStream != null) {
				try {
					String serverMsg = dataInputStream.readUTF();
					System.out.println("Server Msg : " + serverMsg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendMessage(String msg) throws IOException {
		dataOutputStream.writeUTF(msg);
	}
}
