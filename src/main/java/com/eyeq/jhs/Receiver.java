package com.eyeq.jhs;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Hana Lee
 * @since 2015-12-18 21:02
 */
class Receiver extends Thread {

	private DataInputStream dataInputStream;

	public Receiver(Socket socket) {
		try {
			this.dataInputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (dataInputStream != null) {
				System.out.println("Msg : " + dataInputStream.readUTF());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
