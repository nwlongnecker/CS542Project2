package indexmechanism;

import java.io.Serializable;

/**
 * Data structure for storing key value pairs
 * @author Nathan
 */
public class Index implements Serializable {
	
	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 5467006958012824144L;
	/**
	 * The key of this index. An identifier that the database could use to find the associated tuple in the database
	 */
	private String key;
	/**
	 * The data value of this index. The indexed value.
	 */
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
