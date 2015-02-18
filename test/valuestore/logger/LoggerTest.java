package valuestore.logger;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import valuestore.ValueStoreException;
import valuestore.ValueStoreImpl;

public class LoggerTest {
	
	Logger logger;
	final String testLoggerDB = "testLoggerDB";
	ValueStoreImpl valueStore;
	
	@Before
	public void setup() throws ValueStoreException {
		valueStore = ValueStoreImpl.getInstance(testLoggerDB);
		logger = new Logger(valueStore);
	}
	
	@After
	public void tearDown() {
		valueStore.cleanUp();
	}

	@Test
	public void testLogTransaction() {
		Transaction t = new WriteTransaction("2", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
	}
	
	@Test
	public void testRemoveTransaction() {
		Transaction t = new WriteTransaction("4", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
		logger.endTransaction(t.getTransactionID());
		assertNull(logger.log.get(t.getTransactionID()));
	}
	
	@Test
	public void testWriteReadEmptyLog() {
		Transaction t = new WriteTransaction("1", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
		logger.endTransaction(t.getTransactionID());
		assertNull(logger.log.get(t.getTransactionID()));
		
		logger = new Logger(valueStore);
		assertEquals(0, logger.log.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteLog() throws FileNotFoundException, IOException, ClassNotFoundException {
		assertEquals(testLoggerDB + "/", valueStore.getDatabaseFolder());
		File f = new File("testLoggerDB/");
		f.mkdir();
		assertTrue(f.exists());
		Transaction t = new WriteTransaction("6", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
		
		HashMap<UUID, Transaction> storedValues;
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(testLoggerDB + "/" + "dblog"))) {
			storedValues = (HashMap<UUID, Transaction>) reader.readObject();
		}
		assertEquals(t.getTransactionID(), storedValues.get(t.getTransactionID()).getTransactionID());
		assertEquals(t.getFilename(), storedValues.get(t.getTransactionID()).getFilename());
	}
}
