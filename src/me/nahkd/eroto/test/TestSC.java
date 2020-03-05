package me.nahkd.eroto.test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import me.nahkd.eroto.ErotoConnection;
import me.nahkd.eroto.ErotoServer;

public class TestSC {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ErotoServer server = new ErotoServer(36969) {
			@Override
			public ErotoConnection createConnection(Socket socket) throws IOException {
				return new ErotoConnection(socket) {
					@Override
					public void processString(String input) {
						System.out.println(input);
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
			}
		};
		server.start();
		
		Socket sock = new Socket("localhost", 36969);
		ErotoConnection con = new ErotoConnection(sock) {};
		con.setup(ErotoConnection.MESSAGETYPE_STRING);
		con.sendString("hello server!");
		sock.close();
		
		while (true) {
			Thread.sleep(1000);
		}
	}

}
