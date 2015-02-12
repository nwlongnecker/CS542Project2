package valuestore;

import java.util.Collection;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import valuestore.logger.Transaction;

/**
 * Class in charge of recovering the database to a consistent state after starting up.
 */
public class Recovery
{
	/**
	 * Processes a collection of unfinished transactions to bring the database to a consistent state.
	 * @param unfinishedTransactions A collection of unfinished transactions to process.
	 * @return Whether the operation was successful.
	 */
	public static boolean process(Collection<Transaction> unfinishedTransactions)
	{
		throw new NotImplementedException();
	}
}
