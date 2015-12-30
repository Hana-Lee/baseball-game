package com.eyeq.jhs.controller;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientBackground {
	private Socket socket;
	private Socket backgroundSocket;
	private DataOutputStream stream;
	private DataInputStream inStream;

	public void connect() {
		System.out.println("Client: Connecting");
		try {
			socket = new Socket("127.0.0.1", 9090);
			System.out.println("Client: connect Status = " + socket.isConnected());
			stream = new DataOutputStream(socket.getOutputStream());
			inStream = new DataInputStream(socket.getInputStream());

			backgroundConnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void backgroundConnect() {
		System.out.println("Client: Background Connecting");
		try {
			backgroundSocket = new Socket("127.0.0.1", 9191);
			System.out.println("Client: connect Status = " + backgroundSocket.isConnected());

			final Receiver receiver = new Receiver(backgroundSocket);
			receiver.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendSocketData(String data) {

//		System.out.println("Client: Sent data : " + data);

		try {
			stream.writeUTF(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getServerMessage() {
		if (inStream != null) {
			try {
				return inStream.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

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
			while (dataInputStream != null) {
				try {
					System.out.println("\nServer auto msg : " + dataInputStream.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
