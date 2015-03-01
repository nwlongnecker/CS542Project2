package indexmechanism;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexMechanismImplTest {

	private static IndexMechanismImpl im;
	private static final String TEST_FOLDER = "testIndexs";
	
	@Before
	public void IndexMechanismImplTestSetup() throws Exception {
		im = IndexMechanismImpl.getInstance(TEST_FOLDER);
	}
	
	@After
	public void IndexMechanismImplTestTeardown() throws IndexMechanismException {
		im.cleanUp();
		IndexMechanismImpl.indexMechanisms.clear();
	}
	
	@Test
	public void testPutThenGet() {
		im.put("myKey", "myDataValue");
		assertEquals("myKey", im.get("myDataValue"));
	}
	
	@Test
	public void testPutSameDataThenGet() {
		im.put("myKey1", "myDataValue");
		im.put("myKey2", "myDataValue");
		assertEquals("myKey1", im.get("myDataValue"));
	}
	
	@Test
	public void testGetEmpty() {
		assertEquals(null, im.get("someData"));
	}
	
	@Test
	public void testPutThenRemove() {
		im.put("key", "dataValue");
		im.remove("key");
		assertNull(im.get("dataValue"));
	}
	
	@Test
	public void testRemove() {
		im.remove("myKeyToRemove");
	}
	
	@Test
	public void testRemoveCorrectOne() {
		im.put("myKey1", "myDataValue1");
		im.put("myKey2", "myDataValue2");
		im.put("myKey3", "myDataValue3");
		im.put("myKey4", "myDataValue4");
		im.put("myKey5", "myDataValue5");
		im.remove("myKey3");
		assertNull(im.get("myDataValue3"));
		assertNotNull(im.get("myDataValue1"));
		assertNotNull(im.get("myDataValue2"));
		assertNotNull(im.get("myDataValue4"));
		assertNotNull(im.get("myDataValue5"));
	}

}
