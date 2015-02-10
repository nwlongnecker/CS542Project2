package valuestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ValueStoreImpl implements IValueStore
{
	final String databaseFolder;
	static final String DEFAULT_LOCATION = "database";
	
	public ValueStoreImpl()
	{
		this(DEFAULT_LOCATION);
	}

	public ValueStoreImpl(String folder)
	{	
		databaseFolder = folder + "/";
		
		// Make the database directory.
		File databaseDir = new File(folder);
		databaseDir.mkdir();
	}
	
	@Override
	public void put(int key, byte[] data)
	{
		File dataFile = new File(databaseFolder + key);
		dataFile.delete();
		
		try (FileOutputStream fileWriter = new FileOutputStream(databaseFolder + key))
		{
			fileWriter.write(data);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] get(int key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(int key)
	{
		// TODO Auto-generated method stub
		
	}
}
