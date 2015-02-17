package valuestore.locks;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import valuestore.locks.LockManager;
import valuestore.locks.LockType;

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
	public void testReadLockMultiple() {
		lockManager.lockKey(1, LockType.READ);
		lockManager.lockKey(1, LockType.READ);
		lockManager.lockKey(1, LockType.READ);
		assertTrue(true);
	}
	
	@Test
	public void testReadThenWrite() {
		lockManager.lockKey(2, LockType.READ);
		lockManager.lockKey(2, LockType.READ);
		lockManager.unlockKey(2, LockType.READ);
		lockManager.unlockKey(2, LockType.READ);
		lockManager.lockKey(2, LockType.WRITE);
		assertTrue(true);
	}
	
	@Test
	public void testWriteLockMultiple() {
		lockManager.lockKey(3, LockType.WRITE);
		lockManager.unlockKey(3, LockType.WRITE);
		lockManager.lockKey(3, LockType.WRITE);
		assertTrue(true);
	}
	
	@Test
	public void testWriteDifferentLocks() {
		lockManager.lockKey(4, LockType.WRITE);
		lockManager.lockKey(5, LockType.WRITE);
		assertTrue(true);
	}

}
