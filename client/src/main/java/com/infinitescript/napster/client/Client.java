package com.infinitescript.napster.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Client() { }
	
	public static Client getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Connect to server.
	 * @param ipAddress - the IP address of the server.
	 * @param username - the username of the user
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect(String ipAddress, String username) 
			throws UnknownHostException, IOException {
		// Ref: http://stackoverflow.com/questions/9305589/how-to-make-client-socket-wait-for-data-from-server-socket
		Socket socket = new Socket(ipAddress, 7777);
		BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		
	}
	
	public static final Client INSTANCE = new Client();
}
