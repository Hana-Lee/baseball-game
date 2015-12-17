package com.eyeq.jhs;


import com.eyeq.jhs.model.Result;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class BaseballGameClient {
	private Socket socket;
	private DataOutputStream stream;
	private DataInputStream inStream;

	public void sendSocketResult(Result result) {

	}

	public void connect() {
		System.out.println("Client: Connecting");
		try {
			socket = new Socket("127.0.0.1", 9999);
			System.out.println("Client: connect Status = " + socket.isConnected());
			stream = new DataOutputStream(socket.getOutputStream());
			inStream = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendSocketData(String data) {

		System.out.println("Client: Sent data");

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

}
