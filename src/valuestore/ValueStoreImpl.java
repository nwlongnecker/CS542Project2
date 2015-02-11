package valuestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import valuestore.logger.DeleteTransaction;
import valuestore.logger.Logger;
import valuestore.logger.WriteTransaction;

/**
 * Represents an implementation of a data store that can keep
 * track of byte data with respect to integer keys.
 */
public class ValueStoreImpl implements IValueStore
{
	// Static string contianing the default database folder location.
	static final String DEFAULT_LOCATION = "database";

	// HashMap to store the ValueStoreImpl objects for each database.
	static final HashMap<String, ValueStoreImpl> valueStores = new HashMap<String, ValueStoreImpl>();
	
	
	// String that contains the path to the database folder.
	final String databaseFolder;
	
	// Lock manager for the value store object.
	final LockManager lockManager;
	

	/**
	 * Constructor that allows the database folder to be specified.
	 * @param folder Path to the folder to store database files.
	 * @throws ValueStoreException If the specified directory is invalid.
	 */
	private ValueStoreImpl(String folder) throws ValueStoreException
	{	
		databaseFolder = folder + "/";
		lockManager = new LockManager();
		
		// Make the database directory.
		File databaseDir = new File(folder);
		databaseDir.mkdir();
		
		File[] files = databaseDir.listFiles();
	    if (files != null) // Some JVMs return null for empty dirs.
	    {
	        for (File f : files)
	        {
	            if (f.isDirectory())
	            {
	                throw new ValueStoreException("Invalid database directory. Database directory cannot contain directories.");
	            }
	        }
	    }
	}
	
	/**
	 * Calls the getInstance method using the default database folder location.
	 * @return The value store for the default folder.
	 * @throws ValueStoreException If the specified directory is invalid.
	 */
	public static ValueStoreImpl getInstance() throws ValueStoreException
	{
		return getInstance(DEFAULT_LOCATION);
	}
	
	/**
	 * Gets the instance of ValueStoreImpl for the given folder.
	 * @param folder The folder location to use for the value store.
	 * @return The value store for the given folder.
	 * @throws ValueStoreException If the specified directory is invalid.
	 */
	public static ValueStoreImpl getInstance(String folder) throws ValueStoreException
	{
		// Check if we already have a ValueStoreImpl for this folder.
		if (valueStores.containsKey(folder))
		{
			// Return the existing ValueStoreImpl object.
			return valueStores.get(folder);
		}
		else
		{
			// Create a new ValueStoreImpl object for the folder.
			ValueStoreImpl valueStore = new ValueStoreImpl(folder);
			
			// Add the object to the HashMap, then return it.
			valueStores.put(folder, valueStore);
			return valueStore;
		}
	}
	
	@Override
	public void put(int key, byte[] data)
	{
		// Log that we're going to do a write.
		UUID opid = Logger.getLogger().logTransaction(new WriteTransaction(databaseFolder + key, data));
		
		// Grab the write lock for the key.
		lockManager.lockKey(key, LockType.WRITE);
		
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
			e.printStackTrace();
		}
		
		// Release the write lock for the key.
		lockManager.unlockKey(key, LockType.WRITE);
		
		// Log that we've finished the write
		Logger.getLogger().endTransaction(opid);
	}

	@Override
	public byte[] get(int key)
	{
		// No logging necessary for read function, just grab the read lock for the key.
		lockManager.lockKey(key, LockType.READ);
		
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
				e.printStackTrace();
			}
			
			// Release the read lock for the key.
			lockManager.unlockKey(key, LockType.READ);
			
			// Return the data from the file.
			return data;
		}
		
		// Release the read lock for the key.
		lockManager.unlockKey(key, LockType.READ);
		
		// There is no data in the value store for this key.
		return null;
	}

	@Override
	public void remove(int key)
	{
		// Log that we're going to do a delete.
		UUID opid = Logger.getLogger().logTransaction(new DeleteTransaction(databaseFolder + key));
		
		// Grab the write lock for the key.
		lockManager.lockKey(key, LockType.WRITE);
		
		// Delete the file for this key if it exists.
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
		
		// Release the write lock for the key.
		lockManager.unlockKey(key, LockType.WRITE);
		
		// Log that we've finished the delete
		Logger.getLogger().endTransaction(opid);
	}
	
	/**
	 * Deletes all traces of the database off the disk. Mostly used for cleaning up after tests.
	 */
	void cleanUp()
	{
		// Delete the database directory.
		File databaseDir = new File(databaseFolder);
		deleteFolder(databaseDir);
	}
	
	/**
	 * Recursively deletes the files in a folder, then the folder itself.
	 * @param folder The folder file to delete.
	 */
	static void deleteFolder(File folder)
	{
	    File[] files = folder.listFiles();
	    if (files != null) // Some JVMs return null for empty dirs.
	    {
	        for (File f : files)
	        {
	            if (f.isDirectory())
	            {
	                deleteFolder(f);
	            }
	            else
	            {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
