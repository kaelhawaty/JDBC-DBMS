package eg.edu.alexu.csd.oop.db.cs2.controller;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.conditions.*;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.*;

public class DatabaseManager implements Database {
    private static DatabaseManager instance = new DatabaseManager();
    private List<DatabaseContainer> databases = new ArrayList<>();
    private DatabaseContainer currentDatabase;
    private FilesHandler filesHandler = new FilesHandler();
    private ConditionsFilter equal = new Equal();
    private ConditionsFilter greater = new GreaterThan();
    private ConditionsFilter less = new LessThan();
    private Switch aSwitch = new Switch();
    private boolean flag;
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
            databases.add(currentDatabase);
            filesHandler.createDatabase(databaseName);
            return true;
        }else if(QueriesParser.checkDropDatabase(query)){
            Pattern regex = Pattern.compile("\\s+(\\w|\\\\)+\\s*;?\\s*$");
            Matcher match = regex.matcher(query);
            match.find();
            String databaseName = match.group().toLowerCase().replaceAll("\\s+", "").replaceAll(";", "");
            if (currentDatabase != null && currentDatabase.getName().equalsIgnoreCase(databaseName)){
                databases.remove(currentDatabase);
                currentDatabase = (databases.size() < 1 ) ? null : databases.get(databases.size()-1);
            }else{
                for (int i = 0; i < databases.size(); ++i){
                    if (databases.get(i).getName().equalsIgnoreCase(databaseName)) {
                        databases.remove(i);
                        break;
                    }
                }
            }
            filesHandler.dropDatabase(databaseName);
            return true;
        } else if (QueriesParser.checkCreateTable(query)) {
            query = query.replaceAll("^\\s*create\\s+table\\s+", "").replaceAll("[\\(\\),;]", " ");
            String[] tableInfo = query.split("\\s+");
            if (filesHandler.isTableExist(tableInfo[0], currentDatabase.getName()))
                return false;
            filesHandler.createTable(tableInfo[0], currentDatabase.getName());
            currentDatabase.addTable(tableInfo);
            flag = true;
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
        Object[][] objects;
        query = query.toLowerCase();
        if (!QueriesParser.checkExecuteQuery(query))
            throw new SQLException();
        if (query.matches("^\\s*select\\s*\\*\\s*from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|(\\'\\w+\\')))?\\s*;?\\s*$")){
            query = query.replaceAll("^\\s*select\\s*\\*\\s*from\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\')$")){
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()))
                    throw new SQLException();
                if (!currentDatabase.getTable(split[0]).containColumn(split[2]))
                    throw new SQLException();
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                objects = selectTable(table.getColumns(), true);
            }else {
                Table table = currentDatabase.getTable(query);
                if(!filesHandler.isTableExist(table.getName(), currentDatabase.getName()))
                    throw new SQLException();
                objects = selectTable(table.getColumns(), true);
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
                if(!filesHandler.isTableExist(table.getName(), currentDatabase.getName()))
                    throw new SQLException();
                for (String columnName : columns){
                    if(!table.containColumn(columnName))
                        throw new SQLException();
                }
                objects = selectTable(table.getColumns(columns), false);
            }else{
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()))
                    throw new SQLException();
                for (String columnName : columns){
                    if(!currentDatabase.getTable(split[0]).containColumn(columnName))
                        throw new SQLException();
                }
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                objects = selectTable(table.getColumns(columns), false);
            }
        }
        return objects;
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        if(currentDatabase == null)
            return 0;
        if (QueriesParser.checkInsertInto(query)){
            HashMap<String, String> hashMap = new HashMap<>();
            if (query.toLowerCase().matches("^\\s*insert\\s+into\\s+\\w+\\s*\\((\\s*\\w+\\s*,)*\\s*\\w+\\s*\\)\\s*values\\s*\\((\\s*([0-9]+|\\'\\w+\\')\\s*,)*\\s*([0-9]+|\\'\\w+\\')\\s*\\)\\s*;?\\s*$")){
                query = query.replaceAll("(?i)^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "");
                query = query.replaceAll("[\\(\\),]", " ");
                String [] split = query.split("\\s+");
                if(!(filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName())))
                    throw  new SQLException();
                if(split.length%2 == 0 && split[split.length/2].equalsIgnoreCase("values")){
                    hashMap.put("ID" ,String.valueOf(currentDatabase.getTable(split[0].toLowerCase()).getIDCounter()));
                   for(int i = 1; i < split.length/2; ++i){
                       if(currentDatabase.containColumn(split[0].toLowerCase(), split[i].toLowerCase()))
                            hashMap.put(split[i].toLowerCase(), split[i+split.length/2]);
                       else
                           throw new SQLException();
                   }
                }else
                    throw new SQLException();
                currentDatabase.insertRow(split[0].toLowerCase(), hashMap);
            }else{
                query = query.replaceAll("(?i)^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "");
                query = query.replaceAll("[\\(\\),]", " ");
                String [] split = query.split("\\s+");
                if(!(filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName())))
                    throw  new SQLException();
                if (split.length-1 != currentDatabase.getTableNumOfColumns(split[0].toLowerCase()))
                    throw new SQLException();
                currentDatabase.insertRow(split[0].toLowerCase(), split);
            }
            return 1;
        }
        else if (QueriesParser.checkDeleteFromTable(query)){
            query = query.toLowerCase();
            query = query.replaceAll("^\\s*delete\\s+from\\s", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+$")){
                if (!filesHandler.isTableExist(query, currentDatabase.getName()))
                    throw new SQLException();
                return currentDatabase.clearTable(query);
            }else{
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()) || !(currentDatabase.getTable(split[0]).containColumn(split[2])))
                    throw new SQLException();
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], split[4]);
                return currentDatabase.deleteItems(split[0], table);
            }
        }
        else if(QueriesParser.checkUpdate(query)){
            query = query.replaceAll("(?i)^\\s*update\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.toLowerCase().matches("^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)$")){
                String[] split = query.replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
                if(!filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName()))
                    throw new SQLException();
                for (int i = 2; i < split.length; i+=2){
                    if (!currentDatabase.getTable(split[0].toLowerCase()).containColumn(split[i].toLowerCase()))
                        throw new SQLException();
                }
                currentDatabase.getTable(split[0].toLowerCase()).updateTable(split);
                return currentDatabase.getTable(split[0].toLowerCase()).getIDCounter();
            }
            else{
                Pattern pattern = Pattern.compile("(?i)^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)");
                Matcher matcher = pattern.matcher(query);
                matcher.find();
                String[] split = matcher.group().replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
                query = query.toLowerCase().replaceAll("^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)", "");
                String[] condition = parseQuery(query);
                if(!filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName()))
                    throw new SQLException();
                for (int i = 2; i < split.length; i+=2){
                    if (!currentDatabase.getTable(split[0].toLowerCase()).containColumn(split[i].toLowerCase()))
                        throw new SQLException();
                }
                Table table = aSwitch.meetCondition(condition[2], currentDatabase.getTable(split[0].toLowerCase()), condition[1], condition[3]);
                table.updateTable(split);
                currentDatabase.getTable(split[0].toLowerCase()).updateTable(table);
                return table.getIDCounter();

            }
        }
        else
            return 0;
    }
    private String[] parseQuery(String query){
        String[] split = new String[5];
        int j = 0;
        split[0] = new String();
        query = query.replaceAll("\\s+", " ");
        for (int i = 0; i < query.length(); ++i) {
            if (query.charAt(i) == ' ' && !(query.charAt(i-1) == '=' || query.charAt(i-1) == '<' || query.charAt(i-1) == '>')) {
                j++;
                split[j] = new String();
            } else if (query.charAt(i) == '=' || query.charAt(i) == '<' || query.charAt(i) == '>') {
                if (query.charAt(i-1) != ' ')
                    j++;
                split[j] = String.valueOf(query.charAt(i));
                j++;
                split[j] = new String();
            } else {
                if(query.charAt(i) == ' ')
                    continue;
                split[j] += String.valueOf(query.charAt(i));
            }
        }
        return split;
    }
    private Object[][] selectTable(List<Column> columns, boolean flag){
        Object[][] objects;
        int i = 0, j = 0;
        if(flag)
            i = 1;
        objects = new Object[columns.get(0).getRecords().size()][columns.size()-i];
        for (Column column : columns) {
            if (flag){
                flag = false;
                continue;
            }
            List<Record> records = column.getRecords();
            i = 0;
            for (Record record : records) {
                objects[i][j] = (record == null) ? null : (column.getType().equalsIgnoreCase("int")) ? Integer.parseInt((String) record.getValue()) : record.getValue();
                i++;
            }
            j++;
        }
        return objects;
    }
}
