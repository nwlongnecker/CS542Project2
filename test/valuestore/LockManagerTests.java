package valuestore;

import static org.junit.Assert.*;

import java.util.concurrent.locks.ReadWriteLock;

import org.junit.Before;
import org.junit.Test;

public class LockManagerTests {
	
	LockManager lockManager;
	
	@Before
	public void setup() {
		lockManager = new LockManager();
	}

	@Test
	public void testInitalize() {
		assertNotNull(lockManager);
	}
	
	@Test
	public void testGetLockForKey() {
		assertNotNull(lockManager.getLockForKey(1));
	}
	
	@Test
	public void testGetSameLockForSameKey() {
		ReadWriteLock lock = lockManager.getLockForKey(1);
		ReadWriteLock lock2 = lockManager.getLockForKey(1);
		assertEquals(lock, lock2);
	}
	
	@Test
	public void testGetDifferentLockForDifferentKey() {
		ReadWriteLock lock = lockManager.getLockForKey(1);
		ReadWriteLock lock2 = lockManager.getLockForKey(2);
		assertNotEquals(lock, lock2);
	}
	
	@Test
	public void testGetLockAfterFinishedGetsDifferentLock() {
		ReadWriteLock lock = lockManager.getLockForKey(1);
		lockManager.finishedWithLockForKey(1);
		ReadWriteLock lock2 = lockManager.getLockForKey(1);
		assertNotEquals(lock, lock2);
	}

}
