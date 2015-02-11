package valuestore;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that manages the locks for a value store.
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
	 * Grab the lock that will be used for reading/writing at given key.
	 * @param key The key representing the data to grab/create the lock for.
	 * @param lockType Whether we are locking for reading or writing.
	 */
	public void lockKey(int key, LockType lockType)
	{
		ReadWriteLock lock;
		
		// Make sure no one else is currently accessing the map.
		synchronized(lockForMap)
		{
			// If the locks map doesn't have a lock for this key.
			if (!locks.containsKey(key))
			{
				// Create a lock for this key.
				locks.put(key, new ReentrantReadWriteLock());
			}
			
			// Grab the appropriate lock.
			lock = locks.get(key);
		}
		
		// Switch on the type of lock we want.
		switch (lockType)
		{
			// Grab the read lock.
			case READ:
				lock.readLock().lock();
				break;
			
			// Grab the write lock.
			case WRITE:
				lock.writeLock().lock();
				break;
		}
	}
	
	/**
	 * Release the lock that was used for reading/writing at given key.
	 * @param key The key representing the data to grab the lock for.
	 * @param lockType Whether we are unlocking after reading or writing.
	 */
	public void unlockKey(int key, LockType lockType)
	{
		ReadWriteLock lock;
		
		// Make sure no one else is currently accessing the map.
		synchronized(lockForMap)
		{
			// Grab the appropriate lock.
			lock = locks.get(key);
		}
		
		// Switch on the type of lock we want.
		switch (lockType)
		{
			// Release the read lock.
			case READ:
				lock.readLock().unlock();
				break;
			
			// Release the write lock.
			case WRITE:
				lock.writeLock().unlock();
				break;
		}
		
		// Make sure no one else is currently accessing the map.
		synchronized(lockForMap)
		{
			// If nobody else is using the lock, remove it to minimize memory usage.
			if (lock.writeLock().tryLock())
			{
				locks.remove(key);
			}
		}
	}
}
