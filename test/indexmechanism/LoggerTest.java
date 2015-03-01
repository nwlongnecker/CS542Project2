package indexmechanism;

import static org.junit.Assert.*;
import indexmechanism.IndexMechanismException;
import indexmechanism.IndexMechanismImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoggerTest {
	
	final String testLoggerDB = "testLoggerDB";
	IndexMechanismImpl indexMechanism;
	
	@Before
	public void setup() throws IndexMechanismException {
		indexMechanism = IndexMechanismImpl.getInstance(testLoggerDB);
	}
	
	@After
	public void tearDown() {
		indexMechanism.cleanUp();
	}
	
	@Test
	public void testWriteAndRead() throws Exception {
		indexMechanism.put("MyKey", "DataValue");
		assertEquals("MyKey", indexMechanism.get("DataValue"));
		IndexMechanismImpl.indexMechanisms.clear();
		indexMechanism = IndexMechanismImpl.getInstance(testLoggerDB);
		assertEquals("MyKey", indexMechanism.get("DataValue"));
	}

//	@Test
//	public void testLogTransaction() {
//		Transaction t = new WriteTransaction("2", "Hello".getBytes());
//		logger.logTransaction(t);
//		assertEquals(t, logger.log.get(t.getTransactionID()));
//	}
//	
//	@Test
//	public void testRemoveTransaction() {
//		Transaction t = new WriteTransaction("4", "Hello".getBytes());
//		logger.logTransaction(t);
//		assertEquals(t, logger.log.get(t.getTransactionID()));
//		logger.endTransaction(t.getTransactionID());
//		assertNull(logger.log.get(t.getTransactionID()));
//	}
//	
//	@Test
//	public void testWriteReadEmptyLog() {
//		Transaction t = new WriteTransaction("1", "Hello".getBytes());
//		logger.logTransaction(t);
//		assertEquals(t, logger.log.get(t.getTransactionID()));
//		logger.endTransaction(t.getTransactionID());
//		assertNull(logger.log.get(t.getTransactionID()));
//		
//		logger = new Logger(indexMechanism);
//		assertEquals(0, logger.log.size());
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testWriteLog() throws FileNotFoundException, IOException, ClassNotFoundException {
//		assertEquals(testLoggerDB + "/", indexMechanism.getDatabaseFolder());
//		File f = new File("testLoggerDB/");
//		f.mkdir();
//		assertTrue(f.exists());
//		Transaction t = new WriteTransaction("6", "Hello".getBytes());
//		logger.logTransaction(t);
//		assertEquals(t, logger.log.get(t.getTransactionID()));
//		
//		HashMap<UUID, Transaction> storedValues;
//		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(testLoggerDB + "/" + "dblog"))) {
//			storedValues = (HashMap<UUID, Transaction>) reader.readObject();
//		}
//		assertEquals(t.getTransactionID(), storedValues.get(t.getTransactionID()).getTransactionID());
//		assertEquals(t.getFilename(), storedValues.get(t.getTransactionID()).getFilename());
//	}
}
