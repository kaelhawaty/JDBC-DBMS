package eg.edu.alexu.csd.oop.db.cs2;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DBLogger {
    private static DBLogger instance;
    private final static Logger logger = Logger.getLogger(DBLogger.getInstance().toString());
    public static DBLogger getInstance(){
        if (instance == null)
            instance = new DBLogger();
        return instance;
    }
    public Logger getLogger(){
        return logger;
    }
    private DBLogger(){
        try {
            FileHandler fh = new FileHandler("log.txt");
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.INFO);
    }
}