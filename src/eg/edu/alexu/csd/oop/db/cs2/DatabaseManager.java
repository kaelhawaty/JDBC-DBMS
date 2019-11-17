package eg.edu.alexu.csd.oop.db.cs2;

import java.sql.SQLException;

public class DatabaseManager implements Database{
    private static DatabaseManager instance = new DatabaseManager();
    public static DatabaseManager getInstance(){
        return instance;
    }
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        return null;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        return false;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        return 0;
    }
}
