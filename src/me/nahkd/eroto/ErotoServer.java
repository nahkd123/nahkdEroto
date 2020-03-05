package me.nahkd.eroto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class ErotoServer extends Thread {
	
	ServerSocket server;
	
	public ErotoServer(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			setDaemon(true);
		}
	}
	
	@Override
	public void run() {
		try {
			while (isAlive()) {
				Socket sock = server.accept();
				System.out.println("Eroto: New connection!");
				ErotoConnection con = createConnection(sock);
				con.setup(ErotoConnection.MESSAGETYPE_STRING);
				con.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract ErotoConnection createConnection(Socket socket) throws IOException;
	
}
