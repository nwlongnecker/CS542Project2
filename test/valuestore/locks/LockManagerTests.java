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
		Thread lockThread1 = new LockThread(1, LockType.READ, 100);
		Thread lockThread2 = new LockThread(1, LockType.READ, 100);
		Thread lockThread3 = new LockThread(1, LockType.READ, 100);
		lockThread1.start();
		lockThread2.start();
		lockThread3.start();
		try {
			lockThread1.join();
			lockThread2.join();
			lockThread3.join();
		} catch (InterruptedException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	@Test
	public void testReadThenWrite() {
		Thread lockThread1 = new LockThread(2, LockType.READ, 1000);
		Thread lockThread2 = new LockThread(2, LockType.READ, 100);
		Thread lockThread3 = new LockThread(2, LockType.WRITE, 100);
		lockThread1.start();
		lockThread2.start();
		lockThread3.start();
		try {
			lockThread1.join();
			lockThread2.join();
			lockThread3.join();
		} catch (InterruptedException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	@Test
	public void testWriteLockMultiple() {
		Thread lockThread1 = new LockThread(3, LockType.WRITE, 1000);
		Thread lockThread2 = new LockThread(3, LockType.WRITE, 1000);
		lockThread1.start();
		lockThread2.start();
		try {
			lockThread1.join();
			lockThread2.join();
		} catch (InterruptedException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	@Test
	public void testWriteDifferentLocks() {
		Thread lockThread1 = new LockThread(4, LockType.WRITE, 1000);
		Thread lockThread2 = new LockThread(5, LockType.WRITE, 1000);
		lockThread1.start();
		lockThread2.start();
		try {
			lockThread1.join();
			lockThread2.join();
		} catch (InterruptedException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	/**
	 * Thread class to lock a certain key, sleep for a variable time, then unlock it.
	 */
	class LockThread extends Thread
	{
		private int key;
		private LockType lockType;
		private int sleepTime;
		
		public LockThread(int key, LockType lockType, int sleepTime)
		{
			this.key = key;
			this.lockType = lockType;
			this.sleepTime = sleepTime;
		}
		
		@Override
		public void run()
		{
			lockManager.lockKey(key, lockType);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lockManager.unlockKey(key, lockType);
		}
	}

}
