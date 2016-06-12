package com.infinitescript.napster.client;

import java.io.*;
import java.net.*;
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
		Runnable commandListenerTask = () -> {
			DatagramSocket commandSocket = null;
			try {
				commandSocket = new DatagramSocket(COMMAND_PORT);
				byte[] inputDataBuffer = new byte[BUFFER_SIZE];
				byte[] outputDataBuffer = new byte[BUFFER_SIZE];

				while ( true ) {
					DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
					commandSocket.receive(inputPacket);
					String command = new String(inputPacket.getData());
					LOGGER.debug("Received new message: " + command);

					if ( command.startsWith("GET ") ) {
						String checksum = command.substring(4, 36);
						String ipAddress = inputPacket.getAddress().toString().substring(1);
						int port = inputPacket.getPort();

						if ( sharedFiles.containsKey(checksum) ) {
							outputDataBuffer = "ACCEPT".getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
							Thread.sleep(1000); // Wait for open socket for receiving files
							sendFileStream(checksum, ipAddress);
						} else {
							outputDataBuffer = "ERROR".getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
						}
					}
				}
			} catch ( Exception ex ) {
				LOGGER.catching(ex);
			} finally {
				if ( commandSocket != null ) {
					commandSocket.close();
				}
			}
		};
		Thread commandListenerThread = new Thread(commandListenerTask);
		commandListenerThread.setDaemon(true);
		commandListenerThread.start();
	}

	private void sendDatagramPacket(DatagramSocket socket, byte[] outputDataBuffer, String ipAddress, int port)
			throws IOException {
		InetAddress inetAddress = InetAddress.getByName(ipAddress);

		DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer, outputDataBuffer.length, inetAddress, port);
		socket.send(outputPacket);
	}

	/**
	 * Stop receiving file stream.
	 */
	public void close() {
		closeSocket(commandListener);
	}

	/**
	 * Send file stream to the receiver.
	 * @param checksum  the checksum of the file
	 * @param ipAddress the IP address of the receiver
	 */
	private void sendFileStream(String checksum, String ipAddress) {
		String filePath = sharedFiles.get(checksum);
		Socket socket = null;
		DataInputStream fileInputStream = null;
		DataOutputStream fileOutputStream = null;

		try {
			socket = new Socket(ipAddress, FILE_STREAM_PORT);
			fileInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			fileOutputStream = new DataOutputStream(socket.getOutputStream());

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
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		} finally {
			// Close Socket and DataStream
			try {
				if ( fileInputStream != null ) {
					fileInputStream.close();
				}
				if ( fileOutputStream != null ) {
					fileOutputStream.close();
				}
				if ( socket != null ) {
					socket.close();
				}
			} catch ( IOException ex ) {
				LOGGER.catching(ex);
			}
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
	 * Check if the shared file requested is available.
	 * @param checksum - the checksum of the file
	 * @return whether the shared file is available
	 */
	public boolean contains(String checksum) {
		return sharedFiles.containsKey(checksum);
	}

	/**
	 * Close socket for the server.
	 * @param socket the server socket to close
	 */
	private void closeSocket(ServerSocket socket) {
		try {
			if ( socket != null ) {
				socket.close();
			}
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		}
	}

	/**
	 * The map is used for storing shared files.
	 *
	 * The key stands for the checksum of the file.
	 * The value stands for the absolute path of the file.
	 */
	private static Map<String, String> sharedFiles = new Hashtable<>();

	/**
	 * The server socket used for receiving commands.
	 */
	private ServerSocket commandListener;

	/**
	 * The port used for receiving commands.
	 */
	private static final int COMMAND_PORT = 7701;

	/**
	 * The port used for sending file stream.
	 */
	private static final int FILE_STREAM_PORT = 7702;

	/**
	 * The buffer size of the file stream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The unique instance of Napster client.
	 */
	public static final FileServer INSTANCE = new FileServer();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileServer.class);
}
