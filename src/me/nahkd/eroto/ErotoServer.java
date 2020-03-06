package me.nahkd.eroto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class ErotoServer extends Thread {
	
	public ServerSocket server;
	boolean shutdownRequest;
	int port;
	
	public ErotoServer(int port) {
		try {
			this.port = port;
			server = new ServerSocket(port);
			shutdownRequest = false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			setDaemon(true);
		}
	}
	
	@Override
	public void run() {
		try {
			while (!shutdownRequest) {
				Socket sock = server.accept();
				if (shutdownRequest) break;
				System.out.println("Eroto: New connection!");
				ErotoConnection con = createConnection(sock);
				con.setup(ErotoConnection.MESSAGETYPE_STRING);
				con.start();
			}
			System.out.println("Eroto Server: Closed");
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		shutdownRequest = true;
		try {
			new Socket("localhost", port).close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			// Wait... localhost is invaild?
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract ErotoConnection createConnection(Socket socket) throws IOException;
	
}
