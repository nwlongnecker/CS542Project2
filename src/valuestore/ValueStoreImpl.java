package valuestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents an implementation of a data store that can keep
 * track of byte data with respect to integer keys.
 */
public class ValueStoreImpl implements IValueStore
{
	// String that contains the path to the database folder.
	private final String databaseFolder;
	
	/**
	 * Default constructor, sets the database folder to "database".
	 */
	public ValueStoreImpl()
	{
		this("database");
	}

	/**
	 * Constructor that allows the database folder to be specified.
	 * @param folder Path to the folder to store database files.
	 */
	public ValueStoreImpl(String folder)
	{	
		databaseFolder = folder + "/";
		
		// Make the database directory.
		File databaseDir = new File(folder);
		databaseDir.mkdir();
	}
	
	@Override
	public void put(int key, byte[] data)
	{
		// Delete the file if it already exists.
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
		
		// Create a new file to store the data.
		try (FileOutputStream fileWriter = new FileOutputStream(databaseFolder + key))
		{
			// Write the data to the new empty file.
			fileWriter.write(data);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] get(int key)
	{
		// Check that a file exists for the key.
		File dataFile = new File(databaseFolder + key);
		if (dataFile.exists())
		{
			// Create a byte array to store the data from the file.
			int dataLength = (int) dataFile.length();
			byte[] data = new byte[dataLength];
			
			// Get a reader for the file containing the data.
			try (FileInputStream fileReader = new FileInputStream(databaseFolder + key))
			{
				// Read in the bytes of the file.
				fileReader.read(data);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Return the data from the file.
			return data;
		}
		
		// There is no data in the value store for this key.
		return null;
	}

	@Override
	public void remove(int key)
	{
		// Delete the file for this key if it exists.
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
	}
}
