package valuestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
		String dirName = "test1";
		ValueStoreImpl vs = new ValueStoreImpl("test1");
		
		int key = 1;
		File newFile = new File(dirName + "/" + key);
		// If the file already exists, delete it
		if(newFile.exists()) {
			assertTrue(newFile.delete());
		}
		assertFalse(newFile.exists());
		
		vs.put(key, "Hello".getBytes());
		
		assertTrue(newFile.exists());
		
//		try(FileReader file = new FileReader("" + key)) {
//			byte[] read
//			assertEquals("Hello", file.r)
//		}
		
		assertTrue(newFile.delete());
	}
}
