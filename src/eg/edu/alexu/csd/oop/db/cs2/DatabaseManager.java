package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.conditions.*;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;
import javafx.collections.ObservableArray;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
        Object[][] objects = null;
        query = query.toLowerCase();
        if (!QueriesParser.checkExecuteQuery(query))
            throw new SQLException();
        if (query.matches("^\\s*select\\s*\\*\\s*from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|(\\'|\\\")\\w+(\\'|\\\")))?\\s*;?\\s*$")){
            query = query.replaceAll("^\\s*select\\s*\\*\\s*from\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\')$")){
                String[] split = parseQuery(query);
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                List<Column> columns = table.getColumns();
                columns.remove(0);
                objects = selectTable(table.getColumns());
            }else {
                Table table = currentDatabase.getTable(query);
                List<Column> columns = table.getColumns();
                columns.remove(0);
                objects = selectTable(columns);
            }
        }else{
            query = query.replaceAll("^\\s*select\\s+", "").replaceAll("\\s*;?\\s*$","");
            Pattern regex = Pattern.compile("^(\\w+\\s*,\\s*)*\\w+\\s+");
            Matcher match = regex.matcher(query);
            match.find();
            String[] columns = match.group().replaceAll(",", " ").split("\\s+");
            query = query.replaceAll("^(\\w+\\s*,\\s*)*\\w+\\s+from\\s+", "");
            if (query.matches("\\w+")){
                Table table = currentDatabase.getTable(query);
                objects = selectTable(table.getColumns(columns));
            }else{
                String[] split = parseQuery(query);
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                objects = selectTable(table.getColumns(columns));
            }
        }
        return objects;
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
                String[] split = parseQuery(query);
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                return currentDatabase.deleteItems(split[0], table);
            }
        }
        return 0;
    }
    private String[] parseQuery(String query){
        String[] split = query.split("\\s+");
        if (split.length != 5) {
            split = new String[5];
            int j = 0;
            split[0] = new String();
            query = query.replaceAll("\\s+", " ");
            for (int i = 0; i < query.length(); ++i) {
                if (query.charAt(i) == ' ') {
                    j++;
                    split[j] = new String();
                } else if (query.charAt(i) == '=' || query.charAt(i) == '<' || query.charAt(i) == '>') {
                    j++;
                    split[j] = String.valueOf(query.charAt(i));
                    j++;
                    split[j] = new String();
                } else {
                    split[j] += String.valueOf(query.charAt(i));
                }
            }
        }
        return split;
    }
    private Object[][] selectTable(List<Column> columns){
        Object[][] objects;
        objects = new Object[columns.get(0).getRecords().size()][columns.size()];
        int i = 0, j = 0;
        for (Column column : columns) {
            List<Record> records = column.getRecords();
            i = 0;
            for (Record record : records) {
                objects[i][j] = record.getValue();
                i++;
            }
            j++;
        }
        return objects;
    }
}
