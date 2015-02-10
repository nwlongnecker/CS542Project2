package valuestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValueStoreImplTests {
	
	@Test
	public void testInitalize() {
		// Create a file to represent the db loc
		String dbLoc = "db";
		File directory = new File(dbLoc);
		// If the directory already exists, delete it
		if(directory.exists()) {
			assertTrue(directory.delete());
		}
		assertFalse(directory.exists());
		// Create a new database location
		ValueStoreImpl vs = new ValueStoreImpl(dbLoc);
		assertEquals("db/", vs.databaseFolder);
		assertTrue(directory.exists());
		// Delete the directory to clean up after the tests
		assertTrue(directory.delete());
	}
	
	@Test
	public void testInitalizeDefault() {
		File directory = new File(ValueStoreImpl.DEFAULT_LOCATION);
		// If the directory already exists, delete it
		if(directory.exists()) {
			assertTrue(directory.delete());
		}
		assertFalse(directory.exists());
		ValueStoreImpl vs = new ValueStoreImpl();
		assertEquals("database/", vs.databaseFolder);
		assertTrue(directory.exists());
		// Delete the directory to clean up after the tests
		assertTrue(directory.delete());
	}

	@Test
	public void testPutNewFile() throws FileNotFoundException, IOException {
		// Test that put creates a new file with specific contents
		String dirName = "test1";
		ValueStoreImpl vs = new ValueStoreImpl(dirName);
		
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
	}
	
	@Test
	public void testPutExistingFile() throws FileNotFoundException, IOException {
		// Test that put replaces an existing file
		String dirName = "test2";
		ValueStoreImpl vs = new ValueStoreImpl(dirName);
		
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
	}
	
	@Test
	public void testPutNonemptyExistingFile() throws FileNotFoundException, IOException {
		// Test that put completely replaces the contents of a file
		String dirName = "test3";
		ValueStoreImpl vs = new ValueStoreImpl(dirName);
		
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
	}
}
