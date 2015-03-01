package indexmechanism.main;

import indexmechanism.IIndexMechanism;
import indexmechanism.IndexMechanismException;
import indexmechanism.IndexMechanismImpl;

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
			IIndexMechanism im = IndexMechanismImpl.getInstance("maindb");
			
			// Put in a couple values for keys.
			im.put("firstKey", "hello");
			im.put("secondKey", "world");
			
			// Retrieve a key for a value and print it out.
			String key = im.get("world");
			System.out.println(new String(key));
			
			// Remove an index.
			im.remove("secondKey");
			
			// Put a new key for the old value.
			im.put("thirdKey", "world");
			
			// Retrieve the new value value and print it out.
			String newKey = im.get("world");
			System.out.println(new String(newKey));
			
		}
		catch (IndexMechanismException e)
		{
			// Only throws an exception if the specified directory is invalid.
			e.printStackTrace();
		}
		
	}

}
