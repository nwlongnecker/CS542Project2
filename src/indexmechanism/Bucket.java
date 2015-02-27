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
	
	private Bucket nextBucket;

	/**
	 * Constructor.
	 * Constructs a new bucket object for storing indices in.
	 */
	public Bucket() {
		set = new HashSet<Index>();
	}
	
	/**
	 * If the bucket is not full, add the specified index to the bucket and return true. Otherwise, return false;
	 * @param toAdd The index to add to the bucket.
	 * @return Returns true if the index was added, false otherwise.
	 */
	public void add(Index toAdd) {
		if (set.size() < MAX_BUCKET_SIZE) {
			set.add(toAdd);
		}
	}
	
	/**
	 * Removes the given key from the bucket if it exists
	 * @param key The key to removed from the bucket
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
		} else {
			return false;
		}
	}
	
	public boolean isFull() {
		return false;
	}
	
	public Bucket split(int order) {
		return null;
	}

}
