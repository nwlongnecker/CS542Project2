package indexmechanism;

/**
 * Represents an index mechanism for a database.
 */
public interface IIndexMechanism
{
	/**
	 * Put data into the index mechanism.
	 * @param key The key to go with the dataValue.
	 * @param dataValue The data to store in the index.
	 */
	void put(String key, String dataValue);
	
	/**
	 * Get data from the index mechanism.
	 * @param dataValue The data to search the index mechanism for.
	 */
	void get(String dataValue);
	
	/**
	 * Remove the data in the index mechanism for the given key.
	 * @param key The key associated with the data to remove.
	 */
	void remove(int key);
}
