package indexmechanism;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BucketTest {
	
	Bucket bucket;
	
	@Before
	public void setup() {
		bucket = new Bucket();
	}

	@Test
	public void testConstructor() {
		assertNotNull(bucket);
		assertEquals(0, bucket.list.size());
		assertNull(bucket.nextBucket);
	}

	@Test
	public void testAddIndex() {
		bucket.add(new Index("",""));
		assertEquals(1, bucket.list.size());
	}

	@Test
	public void testAddIndexBucketOverflow() {
		for (int i = 0; i < 6; i++) {
			bucket.add(new Index("",""));
		}
		assertEquals(4, bucket.list.size());
		assertNotNull(bucket.nextBucket);
		assertEquals(2, bucket.nextBucket.list.size());
	}

	@Test
	public void testGetIndex() {
		Index index = new Index("key","data");
		bucket.add(index);
		assertEquals(index, bucket.get("data"));
	}

	@Test
	public void testGetIndexMultipleValues() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		assertEquals(index2, bucket.get("data2"));
	}

	@Test
	public void testGetIndexNotThere() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		assertNull(bucket.get("datamissing"));
	}

	@Test
	public void testGetIndexFromNextBucketInChain() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		assertEquals(index5, bucket.get("data5"));
	}

	@Test
	public void testBucketHasOverflowFalse() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		assertFalse(bucket.hasOverflow());
	}

	@Test
	public void testBucketHasOverflowTrue() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		assertTrue(bucket.hasOverflow());
	}

	@Test
	public void testRemoveIndexMultipleValues() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		assertTrue(bucket.remove("key2"));
		assertEquals(2, bucket.list.size());
	}

	@Test
	public void testRemoveIndexNotThere() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		assertFalse(bucket.remove("keymissing"));
		assertEquals(3, bucket.list.size());
	}

	@Test
	public void testRemoveIndexFromNextBucketInChain() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		Index index6 = new Index("key6","data5");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		bucket.add(index6);
		assertTrue(bucket.remove("key5"));
		assertEquals(4, bucket.list.size());
		assertEquals(1, bucket.nextBucket.list.size());
	}

	@Test
	public void testRemoveIndexFromNextBucketInChainEmpty() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		assertTrue(bucket.remove("key5"));
		assertEquals(4, bucket.list.size());
		assertNull(bucket.nextBucket);
	}

	@Test
	public void testRemoveIndexCollapsesChain() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		Index index6 = new Index("key6","data6");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		bucket.add(index6);
		assertTrue(bucket.remove("key3"));
		assertEquals(4, bucket.list.size());
		assertNotNull(bucket.nextBucket);
		assertEquals(1, bucket.nextBucket.list.size());
		assertTrue(bucket.remove("key2"));
		assertEquals(4, bucket.list.size());
		assertNull(bucket.nextBucket);
	}

	@Test
	public void testRemoveIndexCollapsesChain2() {
		Index index = new Index("key1","data1");
		Index index2 = new Index("key2","data2");
		Index index3 = new Index("key3","data3");
		Index index4 = new Index("key4","data4");
		Index index5 = new Index("key5","data5");
		Index index6 = new Index("key6","data6");
		Index index7 = new Index("key7","data7");
		Index index8 = new Index("key8","data8");
		Index index9 = new Index("key9","data9");
		Index index10 = new Index("key10","data10");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		bucket.add(index6);
		bucket.add(index7);
		bucket.add(index8);
		bucket.add(index9);
		bucket.add(index10);
		assertTrue(bucket.remove("key2"));
		assertEquals(4, bucket.list.size());
		assertNotNull(bucket.nextBucket);
		assertEquals(4, bucket.nextBucket.list.size());
		assertNotNull(bucket.nextBucket.nextBucket);
		assertEquals(1, bucket.nextBucket.nextBucket.list.size());
		assertTrue(bucket.remove("key3"));
		assertEquals(4, bucket.list.size());
		assertNotNull(bucket.nextBucket);
		assertEquals(4, bucket.nextBucket.list.size());
		assertNull(bucket.nextBucket.nextBucket);
		assertTrue(bucket.remove("key1"));
		assertEquals(4, bucket.list.size());
		assertNotNull(bucket.nextBucket);
		assertEquals(3, bucket.nextBucket.list.size());
		assertNull(bucket.nextBucket.nextBucket);
	}
	
	@Test
	public void testSplitBucketOrder1() {
		Index index = new Index("key1","!");
		Index index2 = new Index("key2","\"");
		bucket.add(index);
		bucket.add(index2);
		Bucket newBucket = bucket.split(1);
		assertNotNull(newBucket);
		assertEquals(1, bucket.list.size());
		assertEquals(1, newBucket.list.size());
		assertNotNull(bucket.get("\""));
		assertNull(bucket.get("!"));
	}
	
	@Test
	public void testSplitBucketOrder12() {
		Index index = new Index("key1","!");
		Index index2 = new Index("key2","#");
		bucket.add(index);
		bucket.add(index2);
		Bucket newBucket = bucket.split(1);
		assertNotNull(newBucket);
		assertEquals(0, bucket.list.size());
		assertEquals(2, newBucket.list.size());
	}
	
	@Test
	public void testSplitBucketOrder2() {
		Index index = new Index("key1","!");
		Index index2 = new Index("key2","#");
		Index index3 = new Index("key3","%");
		Index index4 = new Index("key4","'");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		Bucket newBucket = bucket.split(2);
		assertNotNull(newBucket);
		assertEquals(2, bucket.list.size());
		assertEquals(2, newBucket.list.size());
		assertNull(bucket.get("#"));
		assertNull(bucket.get("'"));
		assertNotNull(bucket.get("!"));
		assertNotNull(bucket.get("%"));
	}

	@Test
	public void testSplit() {
		Index index = new Index("key1","a");
		Index index2 = new Index("key2","b");
		Index index3 = new Index("key3","c");
		Index index4 = new Index("key4","d");
		Index index5 = new Index("key5","e");
		Index index6 = new Index("key6","f");
		Index index7 = new Index("key7","g");
		Index index8 = new Index("key8","h");
		Index index9 = new Index("key9","i");
		Index index10 = new Index("key10","j");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		bucket.add(index6);
		bucket.add(index7);
		bucket.add(index8);
		bucket.add(index9);
		bucket.add(index10);
		Bucket newBucket = bucket.split(1);
		assertNotNull(newBucket);
		assertNotNull(bucket.get("b"));
		assertNotNull(bucket.get("d"));
		assertNotNull(bucket.get("f"));
		assertNotNull(bucket.get("h"));
		assertNotNull(bucket.get("j"));
		assertNotNull(newBucket.get("a"));
		assertNotNull(newBucket.get("c"));
		assertNotNull(newBucket.get("e"));
		assertNotNull(newBucket.get("g"));
		assertNotNull(newBucket.get("i"));
		
		Bucket newBucket2 = bucket.split(2);
		Bucket newBucket3 = newBucket.split(2);
		assertNotNull(bucket.get("d"));
		assertNotNull(bucket.get("h"));
		assertNotNull(newBucket2.get("b"));
		assertNotNull(newBucket2.get("f"));
		assertNotNull(newBucket2.get("j"));
		assertNotNull(newBucket.get("a"));
		assertNotNull(newBucket.get("e"));
		assertNotNull(newBucket.get("i"));
		assertNotNull(newBucket3.get("c"));
		assertNotNull(newBucket3.get("g"));

		Bucket newBucket4 = bucket.split(3);
		Bucket newBucket5 = newBucket.split(3);
		Bucket newBucket6 = newBucket2.split(3);
		Bucket newBucket7 = newBucket3.split(3);
		assertNotNull(bucket.get("h"));
		assertNotNull(newBucket4.get("d"));
		assertNotNull(newBucket2.get("b"));
		assertNotNull(newBucket2.get("j"));
		assertNotNull(newBucket6.get("f"));
		assertNotNull(newBucket.get("a"));
		assertNotNull(newBucket.get("i"));
		assertNotNull(newBucket5.get("e"));
		assertNotNull(newBucket3.get("c"));
		assertNotNull(newBucket7.get("g"));
	}

	@Test
	public void testSplitLongChain() {
		Index index = new Index("key1","a");
		Index index2 = new Index("key2","b");
		Index index3 = new Index("key3","c");
		Index index4 = new Index("key4","d");
		Index index5 = new Index("key5","e");
		Index index6 = new Index("key6","f");
		Index index7 = new Index("key7","g");
		Index index8 = new Index("key8","h");
		Index index9 = new Index("key9","i");
		Index index10 = new Index("key10","j");
		Index index11 = new Index("key1","k");
		Index index12 = new Index("key2","l");
		Index index13 = new Index("key3","m");
		Index index14 = new Index("key4","n");
		Index index15 = new Index("key5","o");
		Index index16 = new Index("key6","p");
		Index index17 = new Index("key7","q");
		Index index18 = new Index("key8","r");
		Index index19 = new Index("key9","s");
		Index index20 = new Index("key10","t");
		Index index21 = new Index("key1","A");
		Index index22 = new Index("key2","B");
		Index index23 = new Index("key3","C");
		Index index24 = new Index("key4","D");
		Index index25 = new Index("key5","E");
		Index index26 = new Index("key6","F");
		Index index27 = new Index("key7","G");
		Index index28 = new Index("key8","H");
		Index index29 = new Index("key9","I");
		Index index30 = new Index("key10","J");
		Index index31 = new Index("key1","K");
		Index index32 = new Index("key2","L");
		Index index33 = new Index("key3","M");
		Index index34 = new Index("key4","N");
		Index index35 = new Index("key5","O");
		Index index36 = new Index("key6","P");
		Index index37 = new Index("key7","Q");
		Index index38 = new Index("key8","R");
		Index index39 = new Index("key9","S");
		Index index40 = new Index("key10","T");
		bucket.add(index);
		bucket.add(index2);
		bucket.add(index3);
		bucket.add(index4);
		bucket.add(index5);
		bucket.add(index6);
		bucket.add(index7);
		bucket.add(index8);
		bucket.add(index9);
		bucket.add(index10);
		bucket.add(index11);
		bucket.add(index12);
		bucket.add(index13);
		bucket.add(index14);
		bucket.add(index15);
		bucket.add(index16);
		bucket.add(index17);
		bucket.add(index18);
		bucket.add(index19);
		bucket.add(index20);
		bucket.add(index21);
		bucket.add(index22);
		bucket.add(index23);
		bucket.add(index24);
		bucket.add(index25);
		bucket.add(index26);
		bucket.add(index27);
		bucket.add(index28);
		bucket.add(index29);
		bucket.add(index30);
		bucket.add(index31);
		bucket.add(index32);
		bucket.add(index33);
		bucket.add(index34);
		bucket.add(index35);
		bucket.add(index36);
		bucket.add(index37);
		bucket.add(index38);
		bucket.add(index39);
		bucket.add(index40);
		Bucket newBucket = bucket.split(1);
		assertNotNull(newBucket);
		assertNotNull(bucket.get("b"));
		assertNotNull(bucket.get("d"));
		assertNotNull(bucket.get("f"));
		assertNotNull(bucket.get("h"));
		assertNotNull(bucket.get("j"));
		assertNotNull(bucket.get("l"));
		assertNotNull(bucket.get("n"));
		assertNotNull(bucket.get("p"));
		assertNotNull(bucket.get("r"));
		assertNotNull(bucket.get("t"));
		assertNotNull(bucket.get("B"));
		assertNotNull(bucket.get("D"));
		assertNotNull(bucket.get("F"));
		assertNotNull(bucket.get("H"));
		assertNotNull(bucket.get("J"));
		assertNotNull(bucket.get("L"));
		assertNotNull(bucket.get("N"));
		assertNotNull(bucket.get("P"));
		assertNotNull(bucket.get("R"));
		assertNotNull(bucket.get("T"));
		assertNotNull(newBucket.get("a"));
		assertNotNull(newBucket.get("c"));
		assertNotNull(newBucket.get("e"));
		assertNotNull(newBucket.get("g"));
		assertNotNull(newBucket.get("i"));
		assertNotNull(newBucket.get("k"));
		assertNotNull(newBucket.get("m"));
		assertNotNull(newBucket.get("o"));
		assertNotNull(newBucket.get("q"));
		assertNotNull(newBucket.get("s"));
		assertNotNull(newBucket.get("A"));
		assertNotNull(newBucket.get("C"));
		assertNotNull(newBucket.get("E"));
		assertNotNull(newBucket.get("G"));
		assertNotNull(newBucket.get("I"));
		assertNotNull(newBucket.get("K"));
		assertNotNull(newBucket.get("M"));
		assertNotNull(newBucket.get("O"));
		assertNotNull(newBucket.get("Q"));
		assertNotNull(newBucket.get("S"));
	}

}
