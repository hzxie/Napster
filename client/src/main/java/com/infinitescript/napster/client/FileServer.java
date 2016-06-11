package com.infinitescript.napster.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * File server for sending shared files to others.
 * 
 * @author Haozhe Xie
 */
public class FileServer {
	private FileServer() { }
	
	public static FileServer getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Receive the message from client.
	 * @throws IOException
	 */
	public void accept() throws IOException {
		ServerSocket commandlistener = new ServerSocket(COMMAND_PORT);
		ServerSocket fileStreamlistener = new ServerSocket(FILE_STREAM_PORT);
		try {
			
		} finally {
			commandlistener.close();
		}
	}
	
	/**
	 * Register new file to the file server for sharing.
	 * 
	 * @param checksum the checksum of the file
	 * @param filePath the absolute path of the file
	 */
	public void shareNewFile(String checksum, String filePath) {
		sharedFiles.put(checksum, filePath);
	}

	/**
	 * Remove a shared file from the file server because it is no longer shared.
	 * @param checksum the checksum of the file
	 */
	public void unshareFile(String checksum) {
		sharedFiles.remove(checksum);
	}
	
	/**
	 * The map is used for storing shared files.
	 * 
	 * The key stands for the checksum of the file.
	 * The value stands for the absolute path of the file. 
	 */
	private static Map<String, String> sharedFiles = new Hashtable<>();
	
	/**
	 * The port used for receiving commands.
	 */
	private static final int COMMAND_PORT = 7701;
	
	/**
	 * The port used for receiving file stream.
	 */
	private static final int FILE_STREAM_PORT = 7702;
	
	/**
	 * The unique instance of Napster client.
	 */
	public static final FileServer INSTANCE = new FileServer();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileServer.class);
}
