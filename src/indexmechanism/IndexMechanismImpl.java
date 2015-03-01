package indexmechanism;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an implementation of a data store that can keep
 * track of byte data with respect to integer keys.
 */
public class IndexMechanismImpl implements IIndexMechanism
{
	// Static string containing the default database folder location.
	static final String DEFAULT_LOCATION = "database";

	// HashMap to store the ValueStoreImpl objects for each database.
	static final HashMap<String, IndexMechanismImpl> indexMechanisms = new HashMap<String, IndexMechanismImpl>();

	// String that contains the path to the database folder.
	final String databaseFolder;

	// Logger for the index mechanism object.
	final Logger logger;

	// Number to keep track of which bucket to split next.
	int roundRobinNum;
	
	// Number to keep track of what the order of the round robin is.
	int roundRobinOrder;
	
	// List to keep all of the buckets for the index mechanism.
	List<Bucket> buckets;

	
	/**
	 * Constructor that allows the database folder to be specified.
	 * @param folder Path to the folder to store database files.
	 * @throws IndexMechanismException If the specified directory is invalid.
	 */
	private IndexMechanismImpl(String folder) throws IndexMechanismException
	{	
		databaseFolder = folder + "/";
		logger = new Logger(this);
		roundRobinNum = 0;
		roundRobinOrder = 2;
		buckets = new ArrayList<Bucket>();

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
					throw new IndexMechanismException("Invalid database directory. Database directory cannot contain directories.");
				}
			}
		}
	}

	/**
	 * Calls the getInstance method using the default database folder location.
	 * @return The value store for the default folder.
	 * @throws IndexMechanismException If the specified directory is invalid.
	 */
	public static IndexMechanismImpl getInstance() throws IndexMechanismException
	{
		return getInstance(DEFAULT_LOCATION);
	}

	/**
	 * Gets the instance of ValueStoreImpl for the given folder.
	 * @param folder The folder location to use for the value store.
	 * @return The value store for the given folder.
	 * @throws IndexMechanismException If the specified directory is invalid.
	 */
	public static synchronized IndexMechanismImpl getInstance(String folder) throws IndexMechanismException
	{
		// Check if we already have a ValueStoreImpl for this folder.
		if (indexMechanisms.containsKey(folder))
		{
			// Return the existing ValueStoreImpl object.
			return indexMechanisms.get(folder);
		}
		else
		{
			// Create a new IndexMechanismImpl object for the folder.
			IndexMechanismImpl indexMechanism = new IndexMechanismImpl(folder);

			// Add four empty buckets to it.
			for (int i = 0; i < 4; i++)
			{
				indexMechanism.buckets.add(new Bucket());
			}

			// Add the object to the HashMap, then return it.
			indexMechanisms.put(folder, indexMechanism);
			return indexMechanism;
		}
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
	 * In case we crashed while using the index mechanism, restore the old bucket list.
	 * @param buckets The already existing list of buckets.
	 * @return Success or failure of the recovery.
	 */
	public boolean recover(List<Bucket> buckets)
	{
		// Set our bucket list to contain the existing buckets.
		this.buckets = buckets;
		
		// For posterity.
		return true;
	}

	@Override
	public void put(String key, String dataValue)
	{	
		// Create a new index to add.
		Index toAdd = new Index(key, dataValue);

		// Use the bucket list as a lock when adding/splitting buckets.
		synchronized (buckets)
		{
			// Figure out which bucket we will add it to.
			int bucketNum = Math.abs(dataValue.hashCode()) % (1 << roundRobinOrder);
			if (bucketNum > buckets.size())
			{
				bucketNum = Math.abs(dataValue.hashCode()) % (1 << (roundRobinOrder - 1));
			}
			
			// Add the index to the bucket.
			buckets.get(bucketNum).add(toAdd);

			// If the addition caused an overflow, split a bucket via round robin.
			if (buckets.get(bucketNum).hasOverflow())
			{
				buckets.add(buckets.get(bucketNum).split(roundRobinOrder));

				// Increment the round robin numbers appropriately.
				roundRobinNum++;
				if (roundRobinNum == ((1 << roundRobinOrder) - 1))
				{
					roundRobinOrder++;
					roundRobinNum = 0;
				}
			}
			
			// Update the log to contain the addition.
			logger.logChange(buckets);
		}
	}

	@Override
	public String get(String dataValue)
	{
		// Use the bucket list as a lock when attempting to get an index.
		synchronized (buckets)
		{
			// Find the bucket that will contain the index for this value if it exists.
			int bucketNum = Math.abs(dataValue.hashCode()) % (1 << roundRobinOrder);
			if (bucketNum > buckets.size())
			{
				bucketNum = Math.abs(dataValue.hashCode()) % (1 << (roundRobinOrder - 1));
			}

			// Return the key for that index if it exists in the bucket.
			Index index = buckets.get(bucketNum).get(dataValue);
			return index == null ? null : index.getKey();
		}
	}

	@Override
	public void remove(String key)
	{
		// Use the bucket list as a lock when attempting to remove an index.
		synchronized (buckets)
		{
			// Iterate through the buckets until we find the one containing
			// the index with the key that we want to remove.
			boolean foundIndex = false;
			for (Bucket bucket : buckets)
			{
				// If we find the bucket with the key, we are done.
				if (bucket.remove(key))
				{
					foundIndex = true;
					break;
				}
			}
			if (foundIndex)
			{
				// Update the log to contain the deletion.
				logger.logChange(buckets);
			}
		}
	}
}
