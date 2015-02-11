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
import valuestore.logger.Logger;

public class LoggerTest {
	
	Logger logger;
	final String logFile = "testLogFile";
	
	@Before
	public void setup() {
		logger = new Logger(logFile);
	}
	
	@After
	public void tearDown() {
		new File(logFile).delete();
		new File(logFile + ".txt").delete();
	}

	@Test
	public void testLoggerIsSingleton() {
		Logger logger = Logger.getLogger();
		Logger logger2 = Logger.getLogger();
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
		
		logger = new Logger(logFile);
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
	
	@Test
	public void testWriteLogPrettyPrint() {
		logger = new Logger("PrettyPrintLog");
		Transaction t = new WriteTransaction("Filename", "Hello".getBytes());
		logger.logTransaction(t);
		t = new DeleteTransaction("name");
		logger.logTransaction(t);
		t = new WriteTransaction("OtherFilename", "LotsOfContentsOftheFilehere".getBytes());
		logger.logTransaction(t);
		new File("PrettyPrintLog").delete();
		new File("PrettyPrintLog.txt").delete();
	}
}
