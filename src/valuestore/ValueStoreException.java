package valuestore;

/**
 * Exception to throw for an error in the value store.
 */
public class ValueStoreException extends Exception
{
	private static final long serialVersionUID = 47506985619361500L;

	public ValueStoreException(String msg)
	{
		super(msg);
	}

}
