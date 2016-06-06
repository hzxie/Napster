package com.infinitescript.napster.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionGateway extends Thread {
	public SessionGateway(Socket socket) {
		this.socket = socket;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			// Decorate the streams so we can send characters
            // and not just bytes.  Ensure output is flushed
            // after every newline.
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			// Get messages from the client, line by line; return them
            // capitalized
            while ( true ) {
            	String command = in.readLine();
            	
            	// Check if the user has logged in
            	if ( !users.containsKey(socket) ) {
            		if ( command.length() <= 7 ||
            				!command.substring(0, 7).equals("CONNECT") ) {
            			out.println("[WARN] Socket is going to close.");
            			closeSocket();
            		} else {
            			String username = command.substring(7);
            			
            			out.println("ACCEPT");
            			users.put(socket, username.trim());
            			LOGGER.info("New user joined " + username + ", Current Online Users: " + users.size());
            		}
            	} else {
            		if ( command.equals("QUIT") ) {
            			// The user is willing to leave
            			users.remove(socket);
            			closeSocket();
            		} else {
            			// Invoke SessionHandler for other request
            		}
            	}
            }
		} catch (IOException e) {
			LOGGER.catching(e);
		} finally {
			closeSocket();
		}
	}
	
	private void closeSocket() {
		if ( socket == null ) {
			return;
		}
		
		try {
			socket.close();
			LOGGER.info("Socket has closed for " + socket);
		} catch ( IOException e ) {
			LOGGER.catching(e);
		}
	}
	
	/**
	 * The socket between the server and client.
	 */
	private Socket socket;
	
	/**
	 * The map used for storing online users.
	 */
	private static Map<Socket, String> users = new Hashtable<Socket, String>();
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(SessionGateway.class);
}
