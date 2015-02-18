package main;

import valuestore.IValueStore;
import valuestore.ValueStoreException;
import valuestore.ValueStoreImpl;

/**
 * Sample main demonstrating how our ValueStore could be used in a program
 * @author Nathan
 */
public class SampleMain {
	
	/**
	 * Main method. Creates a new ValueStore object and puts, gets, and removes some key-value pairs
	 * @param args None necessary
	 */
	public static void main(String[] args) {
		try {
			IValueStore vs = ValueStoreImpl.getInstance("maindb");
			vs.put(4, "Hello world".getBytes());
			vs.put(7, "Key is seven".getBytes());
			vs.get(4);
			vs.remove(4);
			vs.put(5, "value".getBytes());
			
		} catch (ValueStoreException e) {
			// Only throws an exception if the specified directory is invalid
			e.printStackTrace();
		}
		
	}

}
