package com.infinitescript.napster.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileServerTest {
	@BeforeClass
	public static void setUp() throws IOException {
		final InetAddress ADDRESS = InetAddress.getLocalHost();
		final int PORT = 22;
		mockedSocket = new Socket(ADDRESS, PORT);
		
		SharedFile sharedFile = new SharedFile("Shared File #1", "Name of Sharer #1", "Checksum of File #1", 1024);
		fileServer.shareNewFile(sharedFile, mockedSocket);
	}
	
	@Test
	public void testGetFileSharerIpUsingExistingChecksum() throws UnknownHostException {
		String checksum = "Checksum of File #1";
		String ipAddress = fileServer.getFileSharerIp(checksum);
		String localhostIpAddress = InetAddress.getLocalHost().toString();
		
		Assert.assertEquals(localhostIpAddress, ipAddress);
	}
	
	@Test
	public void testGetFileSharerIpUsingChecksumNotExists() throws UnknownHostException {
		String checksum = "Not Existing Checksum";
		String ipAddress = fileServer.getFileSharerIp(checksum);
		
		Assert.assertEquals("ERROR", ipAddress);
	}
	
	@Test
	public void testGetSharedFiles() {
		List<SharedFile> sharedFiles = fileServer.getSharedFiles();
		Assert.assertEquals(1, sharedFiles.size());
		
		SharedFile sharedFile = sharedFiles.get(0);
		Assert.assertEquals("Shared File #1", sharedFile.getFileName());
		Assert.assertEquals(1024, sharedFile.getSize());
	}
	
	@Test
	public void testShareNewFile() throws IOException {
		SharedFile sharedFile = new SharedFile("Shared File #2", "Name of Sharer #2", "Checksum of File #2", 4096);
		Assert.assertEquals(true, fileServer.shareNewFile(sharedFile, mockedSocket));
		
		List<SharedFile> sharedFiles = fileServer.getSharedFiles();
		Assert.assertEquals(2, sharedFiles.size());
	}
	
	@Test
	public void testUnshareFileUsingExistingChecksum() {
		String checksum = "Checksum of File #2";
		Assert.assertEquals(true, fileServer.unshareFile(checksum, mockedSocket));
		
		List<SharedFile> sharedFiles = fileServer.getSharedFiles();
		Assert.assertEquals(1, sharedFiles.size());
		
		SharedFile sharedFile = sharedFiles.get(0);
		Assert.assertEquals("Shared File #1", sharedFile.getFileName());
		Assert.assertEquals(1024, sharedFile.getSize());
	}
	
	@Test
	public void testUnshareFileUsingChecksumNotExists() {
		String checksum = "Not Existing Checksum";
		Assert.assertEquals(false, fileServer.unshareFile(checksum, mockedSocket));
	}
	
	@Test
	public void testUnshareFileUsingChecksumNotMatched() {
		String checksum = "Checksum of File #1";
		Assert.assertEquals(false, fileServer.unshareFile(checksum, new Socket()));
	}
	
	/**
	 * The file server to test.
	 */
	private static final FileServer fileServer = FileServer.getInstance();
	
	/**
	 * The mocked server used for constructing sockets.
	 */
	private static Socket mockedSocket;
}
