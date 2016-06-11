package com.infinitescript.napster.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * The socket listener for server.
 * 
 * @author Haozhe Xie
 */
public class SocketListener {
	/**
	 * The private constructor for singleton pattern.
	 */
	protected SocketListener() { }
	
	public static SocketListener getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Receive the message from client.
	 * @throws IOException
	 */
	public void accept() throws IOException {
		ServerSocket listener = new ServerSocket(PORT);
		try {
			// Listen to incoming sockets
			while ( true ) {
				new SessionGateway(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * The port used for receiving commands from clients.
	 */
	private static final int PORT = 7777;
	
	/**
	 * The unique server instance.
	 */
	private static final SocketListener INSTANCE = new SocketListener();
}
