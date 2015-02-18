package valuestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValueStoreImplTests {
	
	@Test
	public void testInitalize() throws ValueStoreException {
		// Create a file to represent the db loc
		String dbLoc = "db";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(directory.exists()) {
			assertTrue(directory.delete());
		}
		assertFalse(directory.exists());
		// Create a new database location
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dbLoc);
		assertEquals(dbLoc + "/", vs.databaseFolder);
		assertTrue(directory.exists());
		// Delete the directory to clean up after the tests
		assertTrue(directory.delete());
	}
	
	@Test
	public void testInitalizeDefault() throws ValueStoreException {
		File directory = new File(ValueStoreImpl.DEFAULT_LOCATION);
		ValueStoreImpl vs = ValueStoreImpl.getInstance();
		assertEquals("database/", vs.databaseFolder);
		assertTrue(directory.exists());
	}
	
	@Test
	public void testInitalizeFolderAlreadyExists() throws ValueStoreException {
		// Create a file to represent the db loc
		String dbLoc = "db1";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(!directory.exists()) {
			directory.mkdir();
			assertTrue(directory.exists());
		}
		// Create a new database location
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dbLoc);
		assertEquals(dbLoc + "/", vs.databaseFolder);
		assertTrue(directory.exists());
		// Delete the directory to clean up after the tests
		assertTrue(directory.delete());
	}
	
	@Test
	public void testInitalizeFolderContainsDirectories() throws IOException, ValueStoreException {
		// Create a file to represent the db loc
		String dbLoc = "db2";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(!directory.exists()) {
			directory.mkdir();
			assertTrue(directory.exists());
		}
		new File(dbLoc + "/" + 1).mkdir();
		new File(dbLoc + "/" + 3).createNewFile();
		// Create a new database location
		try {
			ValueStoreImpl.getInstance(dbLoc);
			fail();
		} catch (ValueStoreException e) {
			ValueStoreImpl.deleteFolder(directory);
		}
	}
	
	@Test
	public void testCleanUp() throws ValueStoreException {
		String dbLoc = "db3";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(directory.exists()) {
			directory.delete();
		}
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dbLoc);
		assertEquals(dbLoc + "/", vs.databaseFolder);
		assertTrue(directory.exists());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
		assertFalse(directory.exists());
	}
	
	@Test
	public void testCleanUpWithContents() throws IOException, ValueStoreException {
		String dbLoc = "db4";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(directory.exists()) {
			assertTrue(directory.delete());
		}
		assertFalse(directory.exists());
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dbLoc);
		assertEquals(dbLoc + "/", vs.databaseFolder);
		assertTrue(directory.exists());
		new File(dbLoc + "/" + 1).createNewFile();
		new File(dbLoc + "/" + 2).createNewFile();
		// Delete the directory to clean up after the tests
		vs.cleanUp();
		assertFalse(directory.exists());
	}
	
	@Test
	public void testCleanUpWithDirectoriesAsContents() throws IOException, ValueStoreException {
		String dbLoc = "db5";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(directory.exists()) {
			assertTrue(directory.delete());
		}
		assertFalse(directory.exists());
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dbLoc);
		assertEquals(dbLoc + "/", vs.databaseFolder);
		assertTrue(directory.exists());
		new File(dbLoc + "/" + 1).mkdir();
		new File(dbLoc + "/" + 1).createNewFile();
		// Delete the directory to clean up after the tests
		vs.cleanUp();
		assertFalse(directory.exists());
	}

	@Test
	public void testPutNewFile() throws FileNotFoundException, IOException, ValueStoreException {
		// Test that put creates a new file with specific contents
		String dirName = "test1";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 1;
		File newFile = new File(dirName + "/" + key);
		// If the file already exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
		}
		assertFalse(newFile.exists());
		
		byte[] inData = "Hello".getBytes();
		
		vs.put(key, inData);
		
		assertTrue(newFile.exists());
		
		// Create a byte array to store the data from the file.
		int dataLength = (int) newFile.length();
		byte[] outData = new byte[dataLength];
		
		// Get a reader for the file containing the data.
		try (FileInputStream fileReader = new FileInputStream(dirName + "/" + key))
		{
			// Read in the bytes of the file.
			fileReader.read(outData);
		}
		
		assertTrue(Arrays.equals(inData, outData));
		
		assertTrue(newFile.delete());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testPutExistingFile() throws FileNotFoundException, IOException, ValueStoreException {
		// Test that put replaces an existing file
		String dirName = "test2";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 1;
		File newFile = new File(dirName + "/" + key);
		// If the file already exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
			assertFalse(newFile.exists());
		}
		assertTrue(newFile.createNewFile());
		
		byte[] inData = "Hello2".getBytes();
		
		vs.put(key, inData);
		
		assertTrue(newFile.exists());
		
		// Create a byte array to store the data from the file.
		int dataLength = (int) newFile.length();
		byte[] outData = new byte[dataLength];
		
		// Get a reader for the file containing the data.
		try (FileInputStream fileReader = new FileInputStream(dirName + "/" + key))
		{
			// Read in the bytes of the file.
			fileReader.read(outData);
		}
		
		assertTrue(Arrays.equals(inData, outData));
		
		assertTrue(newFile.delete());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testPutNonemptyExistingFile() throws FileNotFoundException, IOException, ValueStoreException {
		// Test that put completely replaces the contents of a file
		String dirName = "test3";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 1;
		File newFile = new File(dirName + "/" + key);
		// If the file already exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
			assertFalse(newFile.exists());
		}
		byte[] startData = "LongDataAlreadyInFile".getBytes();
		try (FileOutputStream fileWriter = new FileOutputStream(dirName + "/" + key))
		{
			// Write the data to the new empty file.
			fileWriter.write(startData);
		}
		
		int dataLength = (int) newFile.length();
		byte[] initData = new byte[dataLength];
		
		// Get a reader for the file containing the data.
		try (FileInputStream fileReader = new FileInputStream(dirName + "/" + key))
		{
			// Read in the bytes of the file.
			fileReader.read(initData);
		}
		
		assertTrue(Arrays.equals(initData, startData));
		
		byte[] inData = "Hello2".getBytes();
		
		vs.put(key, inData);
		
		assertTrue(newFile.exists());
		
		dataLength = (int) newFile.length();
		byte[] outData = new byte[dataLength];
		
		// Get a reader for the file containing the data.
		try (FileInputStream fileReader = new FileInputStream(dirName + "/" + key))
		{
			// Read in the bytes of the file.
			fileReader.read(outData);
		}
		
		assertTrue(Arrays.equals(inData, outData));
		
		assertTrue(newFile.delete());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testRemove() throws IOException, ValueStoreException {
		// Test that remove deletes an existing file
		String dirName = "test4";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 1;
		File newFile = new File(dirName + "/" + key);
		// If the file does not exist, create one
		if(!newFile.exists()) {
			assertTrue(newFile.createNewFile());
			assertTrue(newFile.exists());
		}
		
		vs.remove(1);
		
		assertFalse(newFile.exists());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testRemoveInvalidFile() throws ValueStoreException {
		// Test that remove deletes an existing file
		String dirName = "test5";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 4;
		File newFile = new File(dirName + "/" + key);
		// If the file exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
			assertFalse(newFile.exists());
		}
		
		vs.remove(1);
		
		assertFalse(newFile.exists());
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testGetFile() throws ValueStoreException {
		// Test that get gets the contents of a file
		String dirName = "test6";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 2;
		byte[] inData = "Contents".getBytes();
		vs.put(key, inData);
		
		byte[] outData = vs.get(key);
		
		assertTrue(Arrays.equals(inData, outData));
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
	
	@Test
	public void testGetFileDoesntExist() throws ValueStoreException {
		// Test that get returns null if the file doesn't exist
		String dirName = "test7";
		ValueStoreImpl vs = ValueStoreImpl.getInstance(dirName);
		
		int key = 4;
		File newFile = new File(dirName + "/" + key);
		// If the file exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
			assertFalse(newFile.exists());
		}
		
		byte[] outData = vs.get(key);
		
		assertNull(outData);
		// Delete the directory to clean up after the tests
		vs.cleanUp();
	}
}
