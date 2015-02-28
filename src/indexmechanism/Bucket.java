package indexmechanism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A bucket class for storing sets of indices.
 * @author Nathan
 */
public class Bucket {
	
	/**
	 * A set for storing the indices in this bucket
	 */
	final List<Index> list;
	/**
	 * The maximum size of a bucket
	 */
	public static final int MAX_BUCKET_SIZE = 4;
	
	/**
	 * The next bucket in the linked list of indices
	 */
	Bucket nextBucket;

	/**
	 * Constructor.
	 * Constructs a new bucket object for storing indices in.
	 */
	public Bucket() {
		list = new ArrayList<Index>();
		nextBucket = null;
	}
	
	/**
	 * If the bucket is not full, add the specified index to the bucket.
	 * Otherwise, puts it in the next bucket in the chain.
	 * @param toAdd The index to add to the bucket chain.
	 * @return Returns whether the add was successful
	 */
	public boolean add(Index toAdd) {
		if (list.size() < MAX_BUCKET_SIZE) {
			return list.add(toAdd);
		} else if (nextBucket == null) {
			nextBucket = new Bucket();
			return nextBucket.add(toAdd);
		} else {
			return nextBucket.add(toAdd);
		}
	}
	
	/**
	 * Gets the Index associated with the dataValue from the bucket chain.
	 * @param dataValue The dataValue of the index to get from this bucket chain.
	 * @return Returns true if the index was successfully removed.
	 */
	public Index get(String dataValue) {
		for(Index i : list) {
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
		for(Index i : list) {
			if (i.getKey().equals(key)) {
				toRemove = i;
				break;
			}
		}
		if (toRemove != null) {
			boolean retVal = list.remove(toRemove);
			if (nextBucket != null) {
				Index shiftIndex = nextBucket.getAnIndex();
				nextBucket.remove(shiftIndex.getKey());
				if (nextBucket.list.size() == 0) {
					nextBucket = null;
				}
				list.add(shiftIndex);
			}
			return retVal;
		} else if (nextBucket != null) {
			boolean retVal = nextBucket.remove(key);
			if (nextBucket.list.size() == 0) {
				nextBucket = null;
			}
			return retVal;
		} else {
			return false;
		}
	}
	
	private Index getAnIndex() {
		return list.get(0);
	}

	/**
	 * Checks whether the bucket chain is overflowing.
	 * @return Returns true if this bucket chain is overflowing.
	 */
	public boolean hasOverflow() {
		return nextBucket != null;
	}
	
	/**
	 * Splits the bucket chain on the specified order
	 * @param order The order of the hash to split on
	 * @return Returns the head of a new bucket chain with all the elements with a 1 at the beginning of the new order.
	 */
	public Bucket split(int order) {
		Bucket splitBucket = new Bucket();
		for (int i = 0; i < list.size(); i++) {
			int computeHash = list.get(i).getDataValue().hashCode() % (1 << order);
			if ((computeHash >> (order-1)) == 1) {
				Index toSplit = list.get(i--);
				remove(toSplit.getKey());
				splitBucket.add(toSplit);
			}
		}
		if (nextBucket != null) {
			Bucket nextSplitBucket = nextBucket.split(order);
			while (!nextSplitBucket.list.isEmpty()) {
				Index toMove = nextSplitBucket.list.get(0);
				splitBucket.add(toMove);
				nextSplitBucket.remove(toMove.getKey());
			}
		}
		return splitBucket;
	}

}
