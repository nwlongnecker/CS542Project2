package valuestore;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Singleton class that manages the locks for a value store.
 */
public class LockManager
{
	// Create a map of keys to read/write locks and an object to be
	// used as the lock for that map.
	private final HashMap<Integer, ReadWriteLock> locks;
	private final Object lockForMap;
	
	/**
	 * Default constructor for the lock manager, initialized members.
	 */
	public LockManager()
	{
		locks = new HashMap<Integer, ReadWriteLock>();
		lockForMap = new Object();
	}
	
	/**
	 * Gets the read/write lock for the given key.
	 * @param key Integer to identify the lock needed.
	 * @return The lock for the integer given.
	 */
	public ReadWriteLock getLockForKey(int key)
	{
		// Make sure no one else is currently accessing the map.
		synchronized(lockForMap)
		{
			// If the locks map doesn't have a lock for this key.
			if (!locks.containsKey(key))
			{
				// Create a lock for this key.
				locks.put(key, new ReentrantReadWriteLock());
			}
			// Return the appropriate lock.
			return locks.get(key);
		}
	}
	
	/**
	 * Deletes the lock for a given key so that we don't accumulate
	 * as many locks as there are keys in the value store.
	 * @param key Integer to identify the lock to delete.
	 */
	public void finishedWithLockForKey(int key)
	{
		// Make sure no one else is currently accessing the map.
		synchronized(lockForMap)
		{
			// Remove the lock for this key.
			locks.remove(key);
		}
	}
}
