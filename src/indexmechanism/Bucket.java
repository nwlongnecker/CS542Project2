package indexmechanism;

import java.util.HashSet;
import java.util.Set;

public class Bucket {
	
	private final Set<Index> set;

	public Bucket() {
		set = new HashSet<Index>();
	}
	
	public void add(Index toAdd) {
		set.add(toAdd);
	}
	
	public void remove(String key) {
		
	}
	
	public void split() {
		//IDk what this does yet
	}

}
