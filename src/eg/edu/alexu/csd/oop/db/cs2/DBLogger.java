package eg.edu.alexu.csd.oop.db.cs2;

import java.io.IOException;
import java.util.logging.*;

public class DBLogger {
    private static DBLogger instance;
    private final static Logger logger = Logger.getLogger("GLOBAL");
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
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }
            FileHandler fh = new FileHandler("log.txt");
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.ALL);
    }
}