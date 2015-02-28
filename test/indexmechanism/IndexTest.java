package indexmechanism;

import static org.junit.Assert.*;

import org.junit.Test;

public class IndexTest {

	@Test
	public void testConstructor() {
		Index index = new Index("key", "dataValue");
		assertNotNull(index);
		assertEquals("key", index.getKey());
		assertEquals("dataValue", index.getDataValue());
	}

	@Test
	public void testSetKey() {
		Index index = new Index("location", "index");
		assertEquals("location", index.getKey());
		assertEquals("index", index.getDataValue());
		index.setKey("newLocation");
		assertEquals("newLocation", index.getKey());
		assertEquals("index", index.getDataValue());
	}

	@Test
	public void testSetDataValue() {
		Index index = new Index("location", "index");
		assertEquals("location", index.getKey());
		assertEquals("index", index.getDataValue());
		index.setDataValue("newIndex");
		assertEquals("location", index.getKey());
		assertEquals("newIndex", index.getDataValue());
	}

}
