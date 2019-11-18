package eg.edu.alexu.csd.oop.db.cs2;

import java.sql.SQLException;

public class DatabaseManager implements Database{
    private static DatabaseManager instance = new DatabaseManager();
    private String currentDatabase;
    private FilesHandler filesHandler = new FilesHandler();
    public static DatabaseManager getInstance(){
        return instance;
    }
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        boolean dataBaseExist = filesHandler.isDatabaseExist(databaseName);
        try {
            if(dataBaseExist && dropIfExists) {
                    this.executeStructureQuery("DROP DATABASE"+databaseName);
            }
            else if(dataBaseExist && !dropIfExists){
                return filesHandler.getPathOf(databaseName);
            }
            this.executeStructureQuery("CREATE DATABASE"+databaseName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filesHandler.getPathOf(databaseName);
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
