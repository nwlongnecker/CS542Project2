package indexmechanism.logger;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a database transaction
 * @author Nathan
 */
public class Transaction implements Serializable {

	/**
	 * For serializing
	 */
	private static final long serialVersionUID = 4077074111410750234L;
	
	final String filename;
	final UUID transactionID;
	
	/**
	 * Constructs a transaction
	 * @param filename The file this transaction is associated with
	 */
	public Transaction(String filename) {
		this.filename = filename;
		this.transactionID = UUID.randomUUID();
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the transactionID
	 */
	public UUID getTransactionID() {
		return transactionID;
	}
	
}
