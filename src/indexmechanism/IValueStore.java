package indexmechanism;

/**
 * Represents a value store that stores byte data at integer keys.
 */
public interface IValueStore
{
	/**
	 * Put data into the value store at the given key.
	 * @param key The key that can be used to retrieve the data.
	 * @param data The data to be stored at the key.
	 */
	void put(int key, byte[] data);
	
	/**
	 * Get data from the value store for the given key.
	 * @param key The key to be used to find the data.
	 * @return The data at the given key.
	 */
	byte[] get(int key);
	
	/**
	 * Remove the data in the value store for the given key.
	 * @param key The key to remove the data at.
	 */
	void remove(int key);
}
