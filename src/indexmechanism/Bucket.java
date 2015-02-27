package indexmechanism;

import java.util.HashSet;
import java.util.Set;

/**
 * A bucket class for storing sets of indices.
 * @author Nathan
 */
public class Bucket {
	
	/**
	 * A set for storing the indices in this bucket
	 */
	private final Set<Index> set;
	/**
	 * The maximum size of a bucket
	 */
	public static final int MAX_BUCKET_SIZE = 4;
	
	/**
	 * The next bucket in the linked list of indices
	 */
	private Bucket nextBucket;

	/**
	 * Constructor.
	 * Constructs a new bucket object for storing indices in.
	 */
	public Bucket() {
		set = new HashSet<Index>();
		nextBucket = null;
	}
	
	/**
	 * If the bucket is not full, add the specified index to the bucket.
	 * Otherwise, puts it in the next bucket in the chain.
	 * @param toAdd The index to add to the bucket chain.
	 */
	public void add(Index toAdd) {
		if (set.size() < MAX_BUCKET_SIZE) {
			set.add(toAdd);
		} else if (nextBucket == null) {
			nextBucket = new Bucket();
			nextBucket.add(toAdd);
		} else {
			nextBucket.add(toAdd);
		}
	}
	
	/**
	 * Gets the Index associated with the dataValue from the bucket chain.
	 * @param dataValue The dataValue of the index to get from this bucket chain.
	 * @return Returns true if the index was successfully removed.
	 */
	public Index get(String dataValue) {
		for(Index i : set) {
			if (i.getDataValue().equals(dataValue)) {
				return i;
			}
		}
		if (nextBucket != null) {
			return nextBucket.get(dataValue);
		} else {
			return null;
		}
	}
	
	/**
	 * Removes the given key from the bucket chain if it exists
	 * @param key The key to remove from the bucket chain
	 * @return Returns true if the index was successfully removed.
	 */
	public boolean remove(String key) {
		Index toRemove = null;
		for(Index i : set) {
			if (i.getKey().equals(key)) {
				toRemove = i;
				break;
			}
		}
		if (toRemove != null) {
			return set.remove(toRemove);;
		} else if (nextBucket != null) {
			return nextBucket.remove(key);
		} else {
			return false;
		}
	}
	
	/**
	 * Checks whether the bucket chain is overflowing.
	 * @return Returns true if this bucket chain is overflowing.
	 */
	public boolean hasOverflow() {
		return nextBucket != null;
	}
	
	public Bucket split(int order) {
		return null;
	}

}
