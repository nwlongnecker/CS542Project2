package indexmechanism.logger;

import static org.junit.Assert.*;
import indexmechanism.logger.DeleteTransaction;
import indexmechanism.logger.Transaction;
import indexmechanism.logger.WriteTransaction;

import org.junit.Test;

public class TransactionTest {

	@Test
	public void testConstructorAndGetters() {
		Transaction transaction = new Transaction("NameofFile");
		assertNotNull(transaction);
		assertEquals("NameofFile", transaction.getFilename());
		assertNotNull(transaction.getTransactionID());
	}
	
	@Test
	public void testDeleteTransaction() {
		Transaction transaction = new DeleteTransaction("DeleteTransactionFilename");
		assertNotNull(transaction);
		assertEquals("DeleteTransactionFilename", transaction.getFilename());
		assertNotNull(transaction.getTransactionID());
	}
	
	@Test
	public void testWriteTransaction() {
		byte[] bytes = "Data".getBytes();
		WriteTransaction transaction = new WriteTransaction("WriteTransactionFilename", bytes);
		assertNotNull(transaction);
		assertEquals("WriteTransactionFilename", transaction.getFilename());
		assertEquals(bytes, transaction.getContents());
		assertNotNull(transaction.getTransactionID());
	}

}
