package com.infinitescript.napster.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;

/**
 * File Receiver used for receiving file stream.
 *
 * @author Haozhe Xie
 */
public class FileReceiver {
	private FileReceiver() { }

	public static FileReceiver getInstance() {
		return INSTANCE;
	}

	/**
	 * Receive file stream.
	 * @param checksum  the checksum of the file
	 * @param filePath  the file path to save the file
	 * @param ipAddress the IP address of the sender
	 * @throws Exception
	 */
	public void receiveFile(String checksum, String filePath, String ipAddress) throws Exception {
		DatagramSocket commandSocket = null;
		ServerSocket fileStreamListener = null;
		Socket fileStreamSocket = null;
		DataInputStream fileInputStream = null;
		DataOutputStream fileOutputStream = null;

		try {
			// Send command for requesting files
			commandSocket = new DatagramSocket();
			byte[] inputDataBuffer = new byte[BUFFER_SIZE];
			byte[] outputDataBuffer = ("GET " + checksum).getBytes();
			DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer,
					outputDataBuffer.length, InetAddress.getByName(ipAddress), COMMAND_PORT);
			commandSocket.send(outputPacket);

			DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
			commandSocket.receive(inputPacket);
			String command = new String(inputPacket.getData());

			LOGGER.debug("Receive command from peer: " + command);
			if ( !command.startsWith("ACCEPT") ) {
				throw new Exception("The sharer refused to send this file.");
			}

			// Opening port for receiving file stream
			fileStreamListener = new ServerSocket(FILE_STREAM_PORT);
			fileStreamSocket = fileStreamListener.accept();

			// Receiving Data Stream
			fileInputStream = new DataInputStream(new BufferedInputStream(fileStreamSocket.getInputStream()));
			fileOutputStream = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(filePath))));
			byte[] fileBuffer = new byte[BUFFER_SIZE];
			while ( true ) {
				if ( fileInputStream == null ) {
					return;
				}
				int bytesRead = fileInputStream.read(fileBuffer);

				if ( bytesRead == -1 ) {
					break;
				}
				fileOutputStream.write(fileBuffer, 0, bytesRead);
			}
			fileOutputStream.flush();
		} finally {
			try {
				if ( commandSocket != null ) {
					commandSocket.close();
				}
				if ( fileInputStream != null ) {
					fileInputStream.close();
				}
				if ( fileOutputStream != null ) {
					fileOutputStream.close();
				}
				if ( fileStreamSocket != null ) {
					fileStreamSocket.close();
				}
				if ( fileStreamListener != null ) {
					fileStreamListener.close();
				}
			} catch ( IOException ex ) {
				LOGGER.catching(ex);
			}
		}
	}

	/**
	 * The port used for receiving commands.
	 */
	private static final int COMMAND_PORT = 7701;

	/**
	 * The port used for receiving file stream.
	 */
	private static final int FILE_STREAM_PORT = 7702;

	/**
	 * The buffer size of the file stream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The unique instance of FileReceiver.
	 */
	private static final FileReceiver INSTANCE = new FileReceiver();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileReceiver.class);
}
