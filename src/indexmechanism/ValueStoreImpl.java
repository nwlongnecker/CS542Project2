package indexmechanism;

import indexmechanism.locks.LockManager;
import indexmechanism.locks.LockType;
import indexmechanism.logger.DeleteTransaction;
import indexmechanism.logger.Logger;
import indexmechanism.logger.Transaction;
import indexmechanism.logger.WriteTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

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
	
	// Logger for the value store object.
	final Logger logger;
	
	// Lock for the logger.
	final Object loggerLock;
	

	/**
	 * Constructor that allows the database folder to be specified.
	 * @param folder Path to the folder to store database files.
	 * @throws ValueStoreException If the specified directory is invalid.
	 */
	private ValueStoreImpl(String folder) throws ValueStoreException
	{	
		databaseFolder = folder + "/";
		lockManager = new LockManager();
		logger = new Logger(this);
		loggerLock = new Object();
		
		// Make the database directory.
		File databaseDir = new File(databaseFolder);
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
		UUID opid;
		// Make sure no two threads are logging at the same time.
		synchronized(loggerLock)
		{
			// Log that we're going to do a write.
			opid = logger.logTransaction(new WriteTransaction("" + key, data));
		}
		
		// Write the new data to the key.
		writeKey(key, data);
		
		// Make sure no two threads are logging at the same time.
		synchronized(loggerLock)
		{
			// Log that we've finished the write
			logger.endTransaction(opid);
		}
	}

	@Override
	public byte[] get(int key)
	{
		// No logging necessary for read function, just grab the read lock for the key.
		lockManager.lockKey(key, LockType.READ);
		
		// Check that a file exists for the key.
		File dataFile = new File(databaseFolder + key);
		
		// Get a reader for the file containing the data.
		try (FileInputStream fileReader = new FileInputStream(databaseFolder + key))
		{
			// Create a byte array to store the data from the file.
			int dataLength = (int) dataFile.length();
			
			// Attempt to read until we have all the data.
			byte[] data = null;
			int bytesRead = 0;
			while (bytesRead != dataLength)
			{
				// Check that a file exists for the key.
				if (dataFile.exists())
				{
					data = new byte[dataLength];
					bytesRead = fileReader.read(data);
				}
				else // The file does not exist, return null.
				{
					data = null;
					break;
				}
			}
			
			// Release the read lock for the key.
			lockManager.unlockKey(key, LockType.READ);
			
			// Return the data from the file.
			return data;
		}
		catch (IOException e) {}
		
		// Release the read lock for the key.
		lockManager.unlockKey(key, LockType.READ);
		
		// There is no data in the value store for this key.
		return null;
	}

	@Override
	public void remove(int key)
	{
		UUID opid;
		// Make sure no two threads are logging at the same time.
		synchronized(loggerLock)
		{
			// Log that we're going to do a delete.
			opid = logger.logTransaction(new DeleteTransaction("" + key));
		}
		
		// Delete the key from our value store.
		deleteKey(key);

		// Make sure no two threads are logging at the same time.
		synchronized(loggerLock)
		{
			// Log that we've finished the delete
			logger.endTransaction(opid);
		}
	}
	
	/**
	 * Delete the old key (file) if it exists and create a new one with the data.
	 * @param key The name for the file (an integer).
	 * @param data The bytes to write to the file.
	 */
	private void writeKey(int key, byte[] data)
	{
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
		catch (IOException e) {}
		
		// Release the write lock for the key.
		lockManager.unlockKey(key, LockType.WRITE);
	}
	
	/**
	 * Delete the key (file) if it exists.
	 * @param key The name for the file (an integer).
	 */
	private void deleteKey(int key)
	{
		// Grab the write lock for the key.
		lockManager.lockKey(key, LockType.WRITE);
				
		// Delete the file for this key if it exists.
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
				
		// Release the write lock for the key.
		lockManager.unlockKey(key, LockType.WRITE);
	}
	
	/**
	 * Deletes all traces of the database off the disk. Mostly used for cleaning up after tests.
	 */
	public void cleanUp()
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
	
	/**
	 * Getter for the database folder.
	 * @return String for the database folder.
	 */
	public String getDatabaseFolder()
	{
		return databaseFolder;
	}
	
	/**
	 * In case we crashed while transactions were occurring, recover by executing them.
	 * @param unfinishedTransactions Transactions to complete.
	 * @return Success or failure of the recovery.
	 */
	public boolean recover(Collection<Transaction> unfinishedTransactions)
	{
		// Iterate through the unfinished transactions.
		for (Transaction transaction : unfinishedTransactions)
		{	
			if (transaction instanceof WriteTransaction)
			{
				// If it was a write transaction, write the data to that key.
				writeKey(Integer.parseInt(transaction.getFilename()), ((WriteTransaction) transaction).getContents());
			}
			else if (transaction instanceof DeleteTransaction)
			{
				// If it was a delete transaction, delete the key.
				deleteKey(Integer.parseInt(transaction.getFilename()));
			}
		}
		// For posterity.
		return true;
	}
}
