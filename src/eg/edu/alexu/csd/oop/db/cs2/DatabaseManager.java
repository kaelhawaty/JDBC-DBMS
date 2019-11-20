package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.conditions.*;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.*;

public class DatabaseManager implements Database{
    private static DatabaseManager instance = new DatabaseManager();
    private DatabaseContainer currentDatabase;
    private FilesHandler filesHandler = new FilesHandler();
    private ConditionsFilter equal = new Equal();
    private ConditionsFilter greater = new GreaterThan();
    private ConditionsFilter less = new LessThan();
    private Switch aSwitch = new Switch();
    private DatabaseManager(){
        aSwitch.register("=", equal);
        aSwitch.register("<", less);
        aSwitch.register(">", greater);
    }
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
            currentDatabase = new DatabaseContainer(databaseName);
            filesHandler.createDatabase(databaseName);
            return true;
        }else if(QueriesParser.checkDropDatabase(query)){
            Pattern regex = Pattern.compile("\\s+(\\w|\\\\)+\\s*;?\\s*$");
            Matcher match = regex.matcher(query);
            match.find();
            String databaseName = match.group().toLowerCase().replaceAll("\\s+", "").replaceAll(";", "");
            currentDatabase = null;
            filesHandler.dropDatabase(databaseName);
            return true;
        } else if (QueriesParser.checkCreateTable(query)) {
            query = query.replaceAll("^\\s*create\\s+table\\s+", "").replaceAll("[\\(\\),;]", " ");
            String[] tableInfo = query.split("\\s+");
            if (filesHandler.isTableExist(tableInfo[0], currentDatabase.getName()))
                return false;
            filesHandler.createTable(tableInfo[0], currentDatabase.getName());
            currentDatabase.addTable(tableInfo);
            return true;
        }else if (QueriesParser.checkDropTable(query)){
            query = query.replaceAll("^\\s*drop\\s+table\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(!filesHandler.isTableExist(query, currentDatabase.getName()))
                return false;
            filesHandler.dropTable(query, currentDatabase.getName());
            currentDatabase.removeTable(query);
            return true;
        }else{
            throw new SQLException();
        }
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        query = query.toLowerCase();
        if (QueriesParser.checkInsertInto(query)){
            HashMap<String, String> hashMap = new HashMap<>();
            if (query.matches("^\\s*insert\\s+into\\s+\\w+\\s*\\((\\s*\\w+\\s*,)*\\s*\\w+\\s*\\)\\s*values\\s*\\((\\s*([0-9]+|\\'\\w+\\')\\s*,)*\\s*([0-9]+|\\'\\w+\\')\\s*\\)\\s*;?\\s*$")){
                query = query.replaceAll("^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "");
                query = query.replaceAll("[\\(\\),]", " ");
                String [] split = query.split("\\s+");
                if(!(filesHandler.isTableExist(split[0], currentDatabase.getName())))
                    throw  new SQLException();
                if(split.length%2 == 0 && split[split.length/2].equals("values")){
                    hashMap.put("ID" ,String.valueOf(currentDatabase.getTable(split[0]).getIDCounter()));
                   for(int i = 1; i < split.length/2; ++i){
                       if(currentDatabase.containColumn(split[0], split[i]))
                            hashMap.put(split[i], split[i+split.length/2]);
                       else
                           throw new SQLException();
                   }
                }else
                    throw new SQLException();
                currentDatabase.insertRow(split[0], hashMap);
            }else{
                query = query.replaceAll("^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "");
                query = query.replaceAll("[\\(\\),]", " ");
                String [] split = query.split("\\s+");
                if(!(filesHandler.isTableExist(split[0], currentDatabase.getName())))
                    throw  new SQLException();
                if (split.length-1 != currentDatabase.getTableNumOfColumns(split[0]))
                    throw new SQLException();
                currentDatabase.insertRow(split[0], split);
            }
            return 1;
        }else if (QueriesParser.checkDeleteFromTable(query)){
            query = query.toLowerCase();
            query = query.replaceAll("^\\s*delete\\s+from\\s", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+$")){
                return currentDatabase.clearTable(query);
            }else{
                String[] split = new String[5];
                int j = 0;
                split[0] = new String();
                query = query.replaceAll("\\s+", " ");
                for(int i = 0; i < query.length();++i){
                    if (query.charAt(i)== ' ') {
                        j++;
                        split[j] = new String();
                    }
                    else if(query.charAt(i)=='=' || query.charAt(i) == '<' || query.charAt(i) == '>'){
                        j++;
                        split[j] = String.valueOf(query.charAt(i));
                        j++;
                        split[j] = new String();
                    }else {
                        split[j]+=String.valueOf(query.charAt(i));
                    }
                }
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                return currentDatabase.deleteItems(split[0], table);

            }
        }
        return 0;
    }
}
