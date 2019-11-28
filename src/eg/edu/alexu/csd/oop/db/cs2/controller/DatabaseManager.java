package eg.edu.alexu.csd.oop.db.cs2.controller;

import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.conditions.*;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.XML;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.*;

public class DatabaseManager implements Database {
    private static DatabaseManager instance;
    private DatabaseContainer currentDatabase;
    private FilesHandler filesHandler;
    private Switch aSwitch;
    private boolean flag;
    private DatabaseManager(){
        filesHandler = new FilesHandler();
        aSwitch = new Switch();
        instance = this;
    }
    public synchronized static DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
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
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return filesHandler.getPathOf(databaseName);
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException, IOException {
        query = query.toLowerCase();
        if(currentDatabase == null && !QueriesParser.checkCreateDatabase(query) && !QueriesParser.checkDropDatabase(query) ){
            throw new SQLException("There is no current open Database!");
        }
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
            currentDatabase.addTable(tableInfo);
            filesHandler.saveTable(currentDatabase.getTable(tableInfo[0]));
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
            throw new SQLException("Synatx Error");
        }
    }
    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        if(currentDatabase == null){
            return null;
        }
        Object[][] objects;
        query = query.toLowerCase();
        if (!QueriesParser.checkExecuteQuery(query))
            throw new SQLException("Synatx Error");
        Pattern p = Pattern.compile("\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?");
        Matcher m = p.matcher(query);
        String order = null;
        String tableName;
        if(m.find()){
            order = m.group();
            query = query.replaceAll("\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?", "");
        }
        if (query.matches("^\\s*select\\s*\\*\\s*from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|(\\'\\w+\\')))?\\s*;?\\s*$")){
            query = query.replaceAll("^\\s*select\\s*\\*\\s*from\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\')$")){
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase.getName());
                if (!currentDatabase.getTable(split[0]).containColumn(split[2]))
                    throw new SQLException("Column " + split[2] + "doesn't exist in table" + split[0]);
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], Factory.getInstance().getObject(split[4]));
                objects = selectTable(table.getColumns(), true);
                tableName = split[0];
            }else {
                if(!filesHandler.isTableExist(query, currentDatabase.getName()))
                    throw new SQLException("Table " + query + " doesn't exist in database" + currentDatabase.getName());
                Table table = currentDatabase.getTable(query);
                objects = selectTable(table.getColumns(), true);
                tableName = table.getName();
            }
        }else{
            query = query.replaceAll("^\\s*select\\s+", "").replaceAll("\\s*;?\\s*$","");
            Pattern regex = Pattern.compile("^(\\w+\\s*,\\s*)*\\w+\\s+");
            Matcher match = regex.matcher(query);
            match.find();
            String[] columns = match.group().replaceAll(",", " ").split("\\s+");
            query = query.replaceAll("^(\\w+\\s*,\\s*)*\\w+\\s+from\\s+", "");
            if (query.matches("\\w+")){
                if(!filesHandler.isTableExist(query, currentDatabase.getName()))
                    throw new SQLException("Table " + query + " doesn't exist in database" + currentDatabase.getName());
                Table table = currentDatabase.getTable(query);
                for (String columnName : columns){
                    if(!table.containColumn(columnName))
                        throw new SQLException("Column " + columnName + "doesn't exist in table" + table.getName());
                }
                objects = selectTable(table.getColumns(columns), false);
                tableName = table.getName();
            }else{
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase.getName());
                for (String columnName : columns){
                    if(!currentDatabase.getTable(split[0]).containColumn(columnName))
                        throw new SQLException("Column " + columnName + "doesn't exist in table" + split[0]);
                }
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], Factory.getInstance().getObject(split[4]));
                objects = selectTable(table.getColumns(columns), false);
                tableName = table.getName();
            }
        }
        if(order != null){
            objects = sortTable(objects, order, tableName);
        }
        return objects;
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException, IOException {
        if(currentDatabase == null)
            return 0;
        if (QueriesParser.checkInsertInto(query)){
            HashMap<String, Object> hashMap = new HashMap<>();
            if (query.toLowerCase().matches("^\\s*insert\\s+into\\s+\\w+\\s*\\((\\s*\\w+\\s*,)*\\s*\\w+\\s*\\)\\s*values\\s*\\((\\s*([0-9]+|\\'\\w+\\')\\s*,)*\\s*([0-9]+|\\'\\w+\\')\\s*\\)\\s*;?\\s*$")){
                query = query.replaceAll("(?i)^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "");
                query = query.replaceAll("[\\(\\),]", " ");
                String [] split = query.split("\\s+");
                if(!(filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName())))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase.getName());
                if(split.length%2 == 0 && split[split.length/2].equalsIgnoreCase("values")){
                    hashMap.put("ID" ,currentDatabase.getTable(split[0].toLowerCase()).getIDCounter());
                   for(int i = 1; i < split.length/2; ++i){
                       if(currentDatabase.containColumn(split[0].toLowerCase(), split[i].toLowerCase())) {
                           hashMap.put(split[i].toLowerCase(), Factory.getInstance().getObject(split[i + split.length / 2]));
                       }else
                           throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
                   }
                }else
                    throw new SQLException("Syntax Error");
                currentDatabase.insertRow(split[0].toLowerCase(), hashMap);
                filesHandler.saveTable(currentDatabase.getTable(split[0].toLowerCase()));
            }else{
                query = query.replaceAll("(?i)^\\s*insert\\s+into\\s+", "").replaceAll("\\s*;?\\s*$", "").replaceAll("(?i)(values)", "");
                query = query.replaceAll("[(\\),]", " ");
                String [] split = query.split("\\s+");
                Object[] values = new Object[split.length-1];
                for(int i = 1; i < split.length; i++){
                    values[i-1] = Factory.getInstance().getObject(split[i]);
                }
                if(!(filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName())))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database " + currentDatabase.getName());
                if (split.length != currentDatabase.getTableNumOfColumns(split[0].toLowerCase()))
                    throw new SQLException("Syntax Error");
                currentDatabase.insertRow(split[0].toLowerCase(), values);
                filesHandler.saveTable(currentDatabase.getTable(split[0].toLowerCase()));
            }
            return 1;
        }
        else if (QueriesParser.checkDeleteFromTable(query)){
            query = query.toLowerCase();
            query = query.replaceAll("^\\s*delete\\s+from\\s", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+$")){
                if (!filesHandler.isTableExist(query, currentDatabase.getName()))
                    throw new SQLException("Table " + query + " doesn't exist in database" + currentDatabase.getName());
                int ans = currentDatabase.clearTable(query);
                filesHandler.saveTable(currentDatabase.getTable(query));
                return ans;
            }else{
                String[] split = parseQuery(query);
                if(!filesHandler.isTableExist(split[0], currentDatabase.getName()))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase.getName());
                if(!(currentDatabase.getTable(split[0]).containColumn(split[2])))
                    throw new SQLException("Column " + split[2] + "doesn't exist in table" + split[0]);
                Table table = aSwitch.meetCondition(split[3], currentDatabase.getTable(split[0]), split[2], Factory.getInstance().getObject(split[4]));
                int ans = currentDatabase.deleteItems(split[0], table);
                filesHandler.saveTable(currentDatabase.getTable(split[0]));
                return ans;
            }
        }
        else if(QueriesParser.checkUpdate(query)){
            query = query.replaceAll("(?i)^\\s*update\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.toLowerCase().matches("^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)$")){
                String[] split = query.replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
                if(!filesHandler.isTableExist(split[0].toLowerCase(), currentDatabase.getName()))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase.getName());
                for (int i = 2; i < split.length; i+=2){
                    if (!currentDatabase.getTable(split[0].toLowerCase()).containColumn(split[i].toLowerCase()))
                        throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
                }
                Object[] vals = new Object[split.length-2];
                for(int i = 2; i < split.length; i+= 2){
                    vals[i-2] = split[i];
                    vals[i-1] = Factory.getInstance().getObject(split[i+1]);
                }
                currentDatabase.getTable(split[0].toLowerCase()).updateTable(vals);
                filesHandler.saveTable(currentDatabase.getTable(split[0].toLowerCase()));
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
                        throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
                }
                Object[] vals = new Object[split.length-2];
                for(int i = 2; i < split.length; i+= 2){
                    vals[i-2] = split[i];
                    vals[i-1] = Factory.getInstance().getObject(split[i+1]);
                }
                Table table = aSwitch.meetCondition(condition[2], currentDatabase.getTable(split[0].toLowerCase()), condition[1], Factory.getInstance().getObject(condition[3]));
                table.updateTable(vals);
                currentDatabase.getTable(split[0].toLowerCase()).updateTable(table);
                filesHandler.saveTable(currentDatabase.getTable(split[0].toLowerCase()));
                return table.getIDCounter();

            }
        }
        else
            throw new SQLException("Syntax Error");
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
                objects[i][j] = (record == null) ? null : record.getValue();
                i++;
            }
            j++;
        }
        return objects;
    }
    public Object[][] sortTable(Object[][] table, String input, String tableName) throws SQLException {
        List<Integer> idx = new ArrayList<>();
        input = input.replaceAll("\\s+order\\s+by\\s+", "").replaceAll(",", " ");
        String[] split = input.split("\\s+");
        List<Column> temp = currentDatabase.getTable(tableName).getColumns();
        for(int i = 0; i < split.length; i++){
            if(split[i].equals("desc")){
                idx.set(idx.size()-1 ,idx.get(idx.size()-1) *-1);
                continue;
            }else if(split[i].equals("asc")){
               continue;
            }
            int j;
            for(j = 1; j < temp.size(); j++){
                if(temp.get(j).getName().equals(split[i])){
                    break;
                }
            }
            if(j ==  temp.size()){
                throw new SQLException("While sorting: A column was not found!");
            }
            idx.add(j);
        }
        Arrays.sort(table, (Object[] a, Object[] b) -> {
            int i = 0;
            int cmp = 0;
            while(i < idx.size()){
                cmp = Factory.getInstance().compareObject(a[Math.abs(idx.get(i))-1], b[Math.abs(idx.get(i))-1]);
                if(cmp != 0){
                    return (idx.get(i)/Math.abs(idx.get(i))) * cmp;
                }
                i++;
            }
            return cmp;
        });
        return table;
    }

    public DatabaseContainer getCurrentDatabase() {
        return currentDatabase;
    }
}
