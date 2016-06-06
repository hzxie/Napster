package com.infinitescript.napster.server;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The entrance of the application.
 * 
 * @author Haozhe Xie
 */
public class ApplicationBootstrap {
	public static void main(String[] args) {
		SocketListener listener = SocketListener.getInstance();
		
		try {
			LOGGER.info("Server is running...");
			listener.accept();
		} catch (IOException e) {
			LOGGER.catching(e);
		}
	}
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);
}
