package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.structures.DatabaseContainer;

import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseManager implements Database{
    private static DatabaseManager instance = new DatabaseManager();
    private DatabaseContainer currentDatabase;
    private FilesHandler filesHandler = new FilesHandler();

    public static DatabaseManager getInstance(){
        return instance;
    }
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        databaseName = databaseName.toLowerCase();
        boolean dataBaseExist = filesHandler.isDatabaseExist(databaseName);
        try {
            if(dataBaseExist && dropIfExists) {
                    this.executeStructureQuery("drop database  "+databaseName);
            }
            else if(dataBaseExist && !dropIfExists){
                return filesHandler.getPathOf(databaseName);
            }
            this.executeStructureQuery("create database  "+databaseName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filesHandler.getPathOf(databaseName);
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        query = query.toLowerCase();
        if(QueriesParser.checkCreateDatabase(query)){
            Pattern regex = Pattern.compile("\\s+(\\w|\\\\)+\\s*;?\\s*$");
            Matcher match = regex.matcher(query);
            match.find();
            String databaseName = match.group().toLowerCase().replaceAll("\\s+", "").replaceAll(";", "");
            filesHandler.createDatabase(databaseName);
        }else if(QueriesParser.checkDropDatabase(query)){
            Pattern regex = Pattern.compile("\\s+(\\w|\\\\)+\\s*;?\\s*$");
            Matcher match = regex.matcher(query);
            match.find();
            String databaseName = match.group().toLowerCase().replaceAll("\\s+", "").replaceAll(";", "");
            filesHandler.dropDatabase(databaseName);
        } else if (QueriesParser.checkCreateTable(query)) {

        }else if (QueriesParser.checkDropTable(query)){

        }else{
            throw new SQLException();
        }
        return true;
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
