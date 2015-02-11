package valuestore.logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import valuestore.Recovery;
import valuestore.ValueStoreException;

/**
 * Singleton class for logging changes to the database. Used to restore to a consistent state on startup.
 * @author Nathan
 */
public class Logger {
	
	static final String LOG_FILE = "dblog";
	
	// Singleton pattern
	private static Logger logger;
	
	/**
	 * Gets the single instance of the logger class
	 * @return A logger
	 * @throws ValueStoreException If recovery from a file fails
	 */
	public static Logger getLogger() {
		if(logger == null) {
			logger = new Logger(LOG_FILE);
		}
		return logger;
	}
	
	HashMap<UUID, Transaction> log;
	final String logFile;
	
	Logger(String logFile) {
		this.logFile = logFile;
		readLog();
		if(log != null) {
			// Check for unfinished transactions and recover
			if(log.size() != 0) {
				// If there are transactions in the log file,
				// we need to complete them to be sure we're in a consistent state
				if(Recovery.process(log.values())) {
					log = new HashMap<UUID, Transaction>();
				} else {
					System.err.println("Recovery failed! Abort!");
					System.exit(1);
				}
			}
		} else {
			log = new HashMap<UUID, Transaction>();
		}
	}
	
	/**
	 * Tell the logger to log the start of a transaction. The logger will write and flush to a file.
	 * @param transaction The transaction to log
	 * @return The unique id of the transaction. Used to tell the logger the transaction has finished.
	 */
	public UUID logTransaction(Transaction transaction) {
		log.put(transaction.getTransactionID(), transaction);
		
		writeLog();
		
		return transaction.getTransactionID();
	}
	
	/**
	 * Tell the logger we've finished a transaction. The logger will remove the transaction from the log file.
	 * @param opno The operation number of the transaction that finished
	 */
	public void endTransaction(UUID opno) {
		log.remove(opno);
		
		writeLog();
	}
	
	/**
	 * Writes and flushes the log HashMap to the log file
	 */
	void writeLog() {
		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(logFile))) {
			writer.writeObject(log);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Purely for convenience, would be removed in production code
		prettyPrintLog();
	}

	/**
	 * Reads the log from a file on the disk
	 */
	@SuppressWarnings("unchecked")
	void readLog() {
		if(new File(logFile).exists()) {
			try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(logFile))) {
				log = (HashMap<UUID, Transaction>) reader.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Pretty prints the log to a file so humans can easily see what's in the log file
	 */
	private void prettyPrintLog() {
		try (FileWriter writer = new FileWriter(logFile + ".txt")) {
			writer.write("Operation ID\t\t\t\t\t\t\t->\tType\tFilename\t(Contents)\n\n");
			for(Transaction t : log.values()) {
				writer.write(t.getTransactionID().toString() + "\t->\t");
				if(t instanceof WriteTransaction) {
					WriteTransaction wt = (WriteTransaction) t;
					writer.write("Write\t");
					writer.write(wt.getFilename() + "\t");
					writer.write(wt.getContents() + "\t");
					
				} else {
					writer.write("Delete\t");
					writer.write(t.getFilename() + "\t");
				}
				writer.write("\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
