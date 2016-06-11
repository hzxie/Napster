package com.infinitescript.napster.client;

import java.io.Serializable;

/**
 * The class is designed for storing information for shared files.
 * 
 * @author Haozhe Xie
 */
public class SharedFile implements Serializable {
	/**
	 * The constructor of this class.
	 * 
	 * @param fileName
	 * @param sharer
	 * @param checksum
	 * @param size
	 */
	public SharedFile(String fileName, String sharer, String checksum, long size) {
		this.fileName = fileName;
		this.sharer = sharer;
		this.checksum = checksum;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public String getSharer() {
		return sharer;
	}

	public String getChecksum() {
		return checksum;
	}

	public long getSize() {
		return size;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format("SharedFile [FileName=%s, Sharer=%s, Checksum=%s, Size=%d Byte]", 
				new Object[] { fileName, sharer, checksum, size });
	}

	/**
	 * The name of the file.
	 */
	private final String fileName;

	/**
	 * The nick name of sharer of the file.
	 */
	private final String sharer;

	/**
	 * The checksum of the file.
	 */
	private final String checksum;

	/**
	 * The size of the file.
	 */
	private final long size;
	
	/**
	 * The unique ID for serializing.
	 */
	private static final long serialVersionUID = -4459827249944645125L;
}