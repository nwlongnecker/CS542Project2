package valuestore.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
	final String logFile = "testLogFile";
	ValueStoreImpl valueStore;
	
	@Before
	public void setup() throws ValueStoreException {
		valueStore = ValueStoreImpl.getInstance();
		logger = new Logger(valueStore);
	}
	
	@After
	public void tearDown() {
		new File(logFile).delete();
		new File(logFile + ".txt").delete();
	}

	@Test
	public void testLoggerIsSingleton() {
		Logger logger = new Logger(valueStore);
		Logger logger2 = new Logger(valueStore);
		assertEquals(logger, logger2);
	}

	@Test
	public void testLogTransaction() {
		Transaction t = new WriteTransaction("Filename", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
	}
	
	@Test
	public void testRemoveTransaction() {
		Transaction t = new WriteTransaction("Filename", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
		logger.endTransaction(t.getTransactionID());
		assertNull(logger.log.get(t.getTransactionID()));
	}
	
	@Test
	public void testWriteReadEmptyLog() {
		Transaction t = new WriteTransaction("Filename", "Hello".getBytes());
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
		Transaction t = new WriteTransaction("Filename2", "Hello".getBytes());
		logger.logTransaction(t);
		assertEquals(t, logger.log.get(t.getTransactionID()));
		
		HashMap<UUID, Transaction> storedValues;
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(logFile))) {
			storedValues = (HashMap<UUID, Transaction>) reader.readObject();
		}
		assertEquals(t.getTransactionID(), storedValues.get(t.getTransactionID()).getTransactionID());
		assertEquals(t.getFilename(), storedValues.get(t.getTransactionID()).getFilename());
	}
}
