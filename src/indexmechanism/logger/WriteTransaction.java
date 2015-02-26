package indexmechanism.logger;

/**
 * Transaction object representing a write transaction
 * @author Nathan
 */
public class WriteTransaction extends Transaction {
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 7773859455180210769L;
	/**
	 * The contents to write to the file
	 */
	byte[] contents;

	/**
	 * Constructs a write transaction
	 * @param filename The file being written to
	 * @param contents The contents being written
	 */
	public WriteTransaction(String filename, byte[] contents) {
		super(filename);
		this.contents = contents;
	}

	/**
	 * @return the contents
	 */
	public byte[] getContents() {
		return contents;
	}

}
