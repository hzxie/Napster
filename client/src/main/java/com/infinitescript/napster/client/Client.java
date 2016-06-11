package com.infinitescript.napster.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
	private Client() { }
	
	public static Client getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Connect to server.
	 * @param ipAddress - the IP address of the server.
	 * @param nickName - the nick name of the user
	 * @throws Exception 
	 */
	public void connect(String ipAddress, String nickName) 
			throws Exception {
		this.ipAddress = ipAddress;
		this.nickName = nickName;
		this.socket = new Socket(ipAddress, PORT);
		this.inputStreamReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		this.outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
		
		// Say hello to server
		outputStreamWriter.println("CONNECT " + nickName);
		
		// Receive ACK from server
		String ackMessage = inputStreamReader.readLine();
		if ( ackMessage.equals("ACCEPT") ) {
			LOGGER.info("Connected to server.");
		} else {
			LOGGER.warn("Server closed socket for unknown reason.");
			throw new Exception("Server closed socket for unknown reason.");
		}
	}
	
	public void disconnect() {
		// Say goodbye to Napster server
		outputStreamWriter.println("QUIT");
		
		// CLose Socket
		try {
			inputStreamReader.close();
			outputStreamWriter.close();
			socket.close();
			
			LOGGER.info("Disconnected from server.");
		} catch (IOException ex) {
			LOGGER.catching(ex);
		}
	}
	
	/**
	 * The ip address of the server.
	 */
	private String ipAddress;
	
	/**
	 * The nick name of the user.
	 */
	private String nickName;
	
	/**
	 * The socket used for communicating with server.
	 */
	private Socket socket;
	
	/**
	 * The reader used for reading input stream. 
	 */
	private BufferedReader inputStreamReader;
	
	/**
	 * The writer used for writing output stream.
	 */
	private PrintWriter outputStreamWriter;
	
	/**
	 * The port of Napster server.
	 */
	private static final int PORT = 7777;
	
	/**
	 * The unique instance of Napster client.
	 */
	public static final Client INSTANCE = new Client();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Client.class);
}
