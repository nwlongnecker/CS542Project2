package valuestore;

import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValueStoreTests {
	
	@Test
	public void valueStoreStressTest() {
		// Create 100 threads
		for(int i = 0; i < 100; i++) {
			// Define the thread object
			Thread t = new Thread() {
				// Define what the thread will do
				public void run() {
					// Try to create a new value store object
					try {
						// Create the database in the stressTest folder
						IValueStore vs = ValueStoreImpl.getInstance("stressTest");
						
						// Make random behavior
						Random r = new Random();
						// Do random operations 10000 times
						for(int j = 0; j < 10000; j++) {
							int operationType = r.nextInt() % 3;
							int key = r.nextInt() % 10;
							// If the operation value is 0, delete a key
							if(operationType == 0) {
								vs.remove(key);
							}
							// If the operation value is 1, put a key
							else if(operationType == 1) {
								// Give the key a value of random length
								int length = Math.abs(r.nextInt()) % 100 + 1;
								byte[] bytes = new byte[length];
								// Fill those bytes with random data
								r.nextBytes(bytes);
								assertNotNull(bytes);
								// Put the key, value pair
								vs.put(key, bytes);
							}
							// Otherwise, get the value there
							else {
								assertNotNull(vs.get(key));
							}
						}
					} catch (ValueStoreException e) {
						e.printStackTrace();
					}
				}
			};
			// Start the thread we just created
			t.start();
		}
	}

}
