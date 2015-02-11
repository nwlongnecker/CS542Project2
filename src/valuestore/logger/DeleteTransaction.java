package valuestore.logger;

/**
 * Transaction object representing a delete transaction
 * @author Nathan
 */
public class DeleteTransaction extends Transaction {

	/**
	 * For logging
	 */
	private static final long serialVersionUID = 4870160516021859560L;

	/**
	 * Constructs a delete transaction
	 * @param filename The file being deleted
	 */
	public DeleteTransaction(String filename) {
		super(filename);
	}

}
