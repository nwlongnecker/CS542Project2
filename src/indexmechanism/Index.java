package indexmechanism;

/**
 * Data structure for storing key value pairs
 * @author Nathan
 */
public class Index {
	
	private String key;
	private String dataValue;

	public Index(String key, String dataValue) {
		this.key = key;
		this.dataValue = dataValue;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the dataValue
	 */
	public String getDataValue() {
		return dataValue;
	}

	/**
	 * @param dataValue the dataValue to set
	 */
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

}
