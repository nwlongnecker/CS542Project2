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
		IndexMechanismImpl.indexMechanisms.clear();
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
	
	@Test
	public void testWriteAndReadRoundRobinNumAndOrder() throws Exception {
		indexMechanism.put("1", "data");
		indexMechanism.put("2", "data");
		indexMechanism.put("3", "data");
		indexMechanism.put("4", "data");
		indexMechanism.put("5", "data");
		indexMechanism.put("6", "data");
		indexMechanism.put("7", "data");
		indexMechanism.put("8", "data");
		indexMechanism.put("9", "data");
		indexMechanism.put("10", "data");
		indexMechanism.put("11", "data");
		indexMechanism.put("12", "data");
		indexMechanism.put("13", "data");
		indexMechanism.put("14", "data");
		indexMechanism.put("15", "data");
		indexMechanism.put("16", "data");
		indexMechanism.put("17", "data");
		indexMechanism.put("18", "data");
		indexMechanism.put("19", "data");
		indexMechanism.put("20", "data");
		IndexMechanismImpl.indexMechanisms.clear();
		indexMechanism = IndexMechanismImpl.getInstance(testLoggerDB);
		assertEquals(5, indexMechanism.roundRobinOrder);
		assertEquals(4, indexMechanism.roundRobinNum);
	}

}
