package indexmechanism;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Class for logging changes to the index mechanism. Used to persist the log.
 * @author Nathan
 */
public class Logger {
	
	static final String LOG_FILE = "ilog";

	final String logFile;
	
	/**
	 * Constructor for a value store logger.
	 * @param databaseFolder The folder for the value store.
	 */
	public Logger(IndexMechanismImpl indexMechanism) {
		this.logFile = indexMechanism.getDatabaseFolder() + LOG_FILE;
		File currentLog = new File(logFile);
		File tempLog = new File(logFile+".tmp");
		if (!currentLog.exists() && tempLog.exists()) {
			tempLog.renameTo(currentLog);
		}
		List<Bucket> listOBuckets = readLog();
		indexMechanism.recover(listOBuckets);
	}
	
	/**
	 * Tell the logger to write the change to a file
	 * @param listOBuckets The current state of the index
	 */
	public void logChange(List<Bucket> listOBuckets) {
		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(logFile+".tmp"))) {
			writer.writeObject(listOBuckets);
			writer.flush();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
		
		File oldLog = new File(logFile);
		oldLog.delete();
		
		File newLog = new File(logFile+".tmp");
		newLog.renameTo(oldLog);
	}

	/**
	 * Reads the log from a file on the disk
	 */
	@SuppressWarnings("unchecked")
	List<Bucket> readLog() {
		if(new File(logFile).exists()) {
			try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(logFile))) {
				return (List<Bucket>) reader.readObject();
			}
			catch (ClassNotFoundException e) {}
			catch (IOException e) {}
		}
		return null;
	}
}
