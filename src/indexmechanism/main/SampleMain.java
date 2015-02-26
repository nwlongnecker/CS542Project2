package indexmechanism.main;

import indexmechanism.IValueStore;
import indexmechanism.ValueStoreException;
import indexmechanism.ValueStoreImpl;

/**
 * Sample main demonstrating how our ValueStore could be used in a program.
 */
public class SampleMain
{	
	/**
	 * Main method. Creates a new ValueStore object and puts, gets, and removes some key-value pairs.
	 * @param args None necessary.
	 */
	public static void main(String[] args)
	{
		try
		{
			// Get an instance of the value store using the folder "maindb".
			IValueStore vs = ValueStoreImpl.getInstance("maindb");
			
			// Put in a couple values for keys.
			vs.put(4, "Hello world".getBytes());
			vs.put(7, "Key is seven".getBytes());
			
			// Retrieve a value and print it out.
			byte[] value = vs.get(4);
			System.out.println(new String(value));
			
			// Remove a value.
			vs.remove(7);
			
			// Put a new value to overwrite an old one.
			vs.put(4, "New value".getBytes());
			
			// Retrieve the new value value and print it out.
			byte[] newValue = vs.get(4);
			System.out.println(new String(newValue));
			
		}
		catch (ValueStoreException e)
		{
			// Only throws an exception if the specified directory is invalid.
			e.printStackTrace();
		}
		
	}

}
