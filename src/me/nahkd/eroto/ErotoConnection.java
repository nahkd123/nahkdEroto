package me.nahkd.eroto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public abstract class ErotoConnection extends Thread {

	public static final int MESSAGETYPE_RAW = 0x00;
	public static final int MESSAGETYPE_STRING = 0x01;
	
	InputStream input;
	OutputStream output;
	Socket socket;
	int messageType;
	boolean closed;
	public final long mid;
	
	public ErotoConnection(Socket socket) throws IOException {
		this.input = socket.getInputStream();
		this.output = socket.getOutputStream();
		this.socket = socket;
		messageType = -1;
		closed = false;
		mid = System.currentTimeMillis();
	}
	
	@Override
	public void run() {
		try {
			int offset;
			while (!closed) {
				if (messageType == -1) {
					messageType = input.read();
					if (messageType == -1) {
						closed = true;
						break;
					}
				}
				
				// Read incoming data
				if (socket.isClosed()) {
					closed = true;
					break;
				}
				int length = (input.read() << 8) + input.read();
				if (length <= -1) {
					closed = true;
					break;
				}
				offset = 0;
				byte[] l = new byte[length];
				while (offset < length) offset += input.read(l, offset, length - offset);
				
				if (messageType == MESSAGETYPE_STRING) {
					processString(new String(l, "UTF-8"));
				} else if (messageType == MESSAGETYPE_RAW) {
					processRaw(l);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Eroto: Connection closed");
	}

	public void processString(String input) {};
	public void processRaw(byte[] input) {};
	
	public void sendString(String output) {
		try {
			byte[] bs = output.getBytes("UTF-8");
			this.output.write(bs.length >> 8);
			this.output.write(bs.length - ((bs.length >> 8) << 8));
			this.output.write(bs);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public final void setup(int msgType) {
		try {
			this.output.write(msgType);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
