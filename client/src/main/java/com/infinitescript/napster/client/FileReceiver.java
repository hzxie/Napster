package com.infinitescript.napster.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * File Receiver used for receiving file stream.
 * @author Haozhe Xie
 */
public class FileReceiver {
	public void accept() {


		ServerSocket fileStreamListener = new ServerSocket(FILE_STREAM_PORT);
		Runnable fileStreamListenerTask = () -> {
			while ( true ) {
				try {
					Socket fileStreamSocket = fileStreamListener.accept();

				} catch ( IOException ex ) {
					LOGGER.catching(ex);
				} finally {
					closeSocket(fileStreamListener);
				}
			}
		};
		new Thread(fileStreamListenerTask).start();
	}

	/**
	 * The port used for receiving file stream.
	 */
	private static final int FILE_STREAM_PORT = 7702;
}
