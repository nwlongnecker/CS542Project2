package indexmechanism;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class IndexMechanismTest {

	@Test
	public void indexMechanismStressTest() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		// Create 100 threads
		for(int i = 0; i < 50; i++) {
			// Define the thread object
			Thread t = new Thread() {
				// Define what the thread will do
				public void run() {
					// Try to create a new index mechanism object
					try {
						// Create the database in the stressTest folder
						IIndexMechanism im = IndexMechanismImpl.getInstance("stressTest");

						// Make random behavior
						Random r = new Random();
						// Do random operations 25 times
						for(int j = 0; j < 10; j++) {
							int operationType = Math.abs(r.nextInt()) % 3;
							String key = Integer.toString(Math.abs(r.nextInt()) % 10);
							// If the operation value is 0, delete a key
							if(operationType == 0) {
								im.remove(key);
							}
							// If the operation value is 1, put a key
							else if(operationType == 1) {
								// Give the key a predictable value
								String value = "value" + key;
								assertNotNull(value);
								// Put the key, value pair
								im.put(key, value);
							}
							// Otherwise, get the value there
							else {
								String value = im.get(key);
								if(value != null) {
									assertEquals("value" + key, value);
								}
							}
						}
					} catch (IndexMechanismException e) {
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
}
