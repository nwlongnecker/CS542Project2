package valuestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
	// String that contains the path to the database folder.
	final String databaseFolder;
	static final String DEFAULT_LOCATION = "database";
	
	/**
	 * Default constructor, sets the database folder to "database".
	 * @throws ValueStoreException If the specified directory is invalid
	 */
	public ValueStoreImpl() throws ValueStoreException
	{
		this(DEFAULT_LOCATION);
	}

	/**
	 * Constructor that allows the database folder to be specified.
	 * @param folder Path to the folder to store database files.
	 * @throws ValueStoreException If the specified directory is invalid
	 */
	public ValueStoreImpl(String folder) throws ValueStoreException
	{	
		databaseFolder = folder + "/";
		
		// Make the database directory.
		File databaseDir = new File(folder);
		databaseDir.mkdir();
		
		File[] files = databaseDir.listFiles();
	    if(files != null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                throw new ValueStoreException("Invalid database directory. Database directory cannot contain directories.");
	            }
	        }
	    }
	}
	
	@Override
	public void put(int key, byte[] data)
	{
		// Log that we're going to do a write
		UUID opid = Logger.getLogger().logTransaction(new WriteTransaction(databaseFolder + key, data));
		
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
		// Log that we've finished the write
		Logger.getLogger().endTransaction(opid);
	}

	@Override
	public byte[] get(int key)
	{
		// No logging necessary for read function
		
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
			
			// Return the data from the file.
			return data;
		}
		
		// There is no data in the value store for this key.
		return null;
	}

	@Override
	public void remove(int key)
	{
		// Log that we're going to do a delete
		UUID opid = Logger.getLogger().logTransaction(new DeleteTransaction(databaseFolder + key));
		
		// Delete the file for this key if it exists.
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
		
		// Log that we've finished the delete
		Logger.getLogger().endTransaction(opid);
	}
	
	/**
	 * Deletes all traces of the database off the disk. Mostly used for cleaning up after tests.
	 */
	void cleanUp() {
		// Delete the database directory.
		File databaseDir = new File(databaseFolder);
		deleteFolder(databaseDir);
	}
	
	static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files != null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
