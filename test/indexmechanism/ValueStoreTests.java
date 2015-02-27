package indexmechanism;

import indexmechanism.IValueStore;
import indexmechanism.ValueStoreException;
import indexmechanism.IndexMechanismImpl;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValueStoreTests {

	@Test
	public void valueStoreStressTest() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		// Create 100 threads
		for(int i = 0; i < 100; i++) {
			// Define the thread object
			Thread t = new Thread() {
				// Define what the thread will do
				public void run() {
					// Try to create a new value store object
					try {
						// Create the database in the stressTest folder
						IValueStore vs = IndexMechanismImpl.getInstance("stressTest");

						// Make random behavior
						Random r = new Random();
						// Do random operations 100 times
						for(int j = 0; j < 100; j++) {
							int operationType = Math.abs(r.nextInt()) % 3;
							int key = Math.abs(r.nextInt()) % 20;
							// If the operation value is 0, delete a key
							if(operationType == 0) {
								vs.remove(key);
							}
							// If the operation value is 1, put a key
							else if(operationType == 1) {
								// Give the key a value of random length
								byte[] bytes = Integer.toString(key).getBytes();
								assertNotNull(bytes);
								// Put the key, value pair
								vs.put(key, bytes);
							}
							// Otherwise, get the value there
							else {
								byte[] value = vs.get(key);
								if(value != null && value.length > 0) {
									assertEquals(Integer.toString(key), new String(value));
								}
							}
						}
					} catch (ValueStoreException e) {
						e.printStackTrace();
					}
				}
			};
			// Start the thread we just created
			t.start();
			threads.add(t);
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void valueStoreSizeTest() {
		try {
			IValueStore vs = IndexMechanismImpl.getInstance("sizeTest");
			byte[] largeData = new byte[500000000];
			vs.put(1, largeData);
			assertTrue(vs.get(1).length == 500000000);
		} catch (ValueStoreException e) {
			e.printStackTrace();
		}
	}
}
