package com.infinitescript.napster.server;

import java.util.*;

/**
 * File Server stored the list of shared files.
 *
 * @author Haozhe Xie
 */
public class FileServer {
	private FileServer() { }

	public static FileServer getInstance() {
		return INSTANCE;
	}

	/**
	 *
	 * @return
	 */
	public List<SharedFile> getSharedFiles() {
		List<SharedFile> sharedFileList = new ArrayList<>();

		for ( Map.Entry<String, Map<String, Object>> e : sharedFiles.entrySet() ) {
			sharedFileList.add((SharedFile) e.getValue().get("sharedFile"));
		}
		return sharedFileList;
	}

	/**
	 * Share a new file to Napster server.
	 * @param sharedFile the file to share
	 * @param ipAddress  the IP address of the sharer
	 * @return whether the share operation is successful
	 */
	public boolean shareNewFile(SharedFile sharedFile, String ipAddress) {
		String checksum = sharedFile.getChecksum();

		if ( sharedFiles.containsKey(checksum) ) {
			return false;
		}

		Map<String, Object> meta = new HashMap<>();
		meta.put("ipAddress", ipAddress);
		meta.put("sharedFile", sharedFile);
		sharedFiles.put(checksum, meta);
		return true;
	}

	private static final Map<String, Map<String, Object>> sharedFiles = new Hashtable<>();

	private static final FileServer INSTANCE = new FileServer();
}
