package eg.edu.alexu.csd.oop.db.cs2.controller;

import eg.edu.alexu.csd.oop.db.cs2.AbstractFactory;
import eg.edu.alexu.csd.oop.db.cs2.Commands.SelectTable;
import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;
import eg.edu.alexu.csd.oop.db.cs2.Factories.StructureFactory;
import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.Factories.UpdateFactory;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.conditions.*;
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
    private String currentDatabase;
    private Table currentTable;
    private Switch aSwitch;
    private DatabaseManager(){
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
        boolean dataBaseExist = FilesHandler.isDatabaseExist(databaseName);
        try {
            if(dataBaseExist && dropIfExists) {
                    this.executeStructureQuery("drop database  "+databaseName);
            }
            else if(dataBaseExist && !dropIfExists){
                return FilesHandler.getPathOf(databaseName);
            }
            this.executeStructureQuery("create database  "+databaseName);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return FilesHandler.getPathOf(databaseName);
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException, IOException {
        query = query.toLowerCase();
        if(currentDatabase == null && !QueriesParser.checkCreateDatabase(query) && !QueriesParser.checkDropDatabase(query) ){
            throw new SQLException("There is no current open Database!");
        }
        return StructureFactory.getInstance().create(query).execute(query) != 0;
    }
    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        if(currentDatabase == null){
            throw new SQLException("There is no current active Database!");
        }
        Object[][] objects;
        query = query.toLowerCase();
        if (!QueriesParser.checkExecuteQuery(query))
            throw new SQLException("Synatx Error");
       Pattern p = Pattern.compile("\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?");
        Matcher m = p.matcher(query);
        String order = null;
        if(m.find()){
            order = m.group();
            query = query.replaceAll("\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?", "");
        }
        objects = new SelectTable().execute(query);
        if(order != null){
            objects = sortTable(objects, order);
        }
        return objects;
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException, IOException {
        if(currentDatabase == null)
            return 0;
        if (QueriesParser.checkInsertInto(query)){
            return UpdateFactory.getInstance().create(query).execute(query);
        }
        else if (QueriesParser.checkDeleteFromTable(query)){
            query = query.toLowerCase();
            query = query.replaceAll("^\\s*delete\\s+from\\s", "").replaceAll("\\s*;?\\s*$", "");
            if(query.matches("^\\w+$")){
                if (!FilesHandler.isTableExist(query, currentDatabase))
                    throw new SQLException("Table " + query + " doesn't exist in database" + currentDatabase);
                int ans = FilesHandler.getTable(query, currentDatabase).clear();
                FilesHandler.saveTable(currentTable);
                return ans;
            }else{
                String[] split = parseQuery(query);
                if(!FilesHandler.isTableExist(split[0], currentDatabase))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase);
                if(!(FilesHandler.getTable(split[0], currentDatabase).containColumn(split[2])))
                    throw new SQLException("Column " + split[2] + "doesn't exist in table" + split[0]);
                Table table = aSwitch.meetCondition(split[3], FilesHandler.getTable(split[0], currentDatabase), split[2], Factory.getInstance().getObject(split[4]));
                int ans = FilesHandler.getTable(split[0], currentDatabase).deleteItems(table);
                FilesHandler.saveTable(currentTable);
                return ans;
            }
        }
        else if(QueriesParser.checkUpdate(query)){
            query = query.replaceAll("(?i)^\\s*update\\s+", "").replaceAll("\\s*;?\\s*$", "");
            if(query.toLowerCase().matches("^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)$")){
                String[] split = query.replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
                if(!FilesHandler.isTableExist(split[0].toLowerCase(), currentDatabase))
                    throw new SQLException("Table " + split[0] + " doesn't exist in database" + currentDatabase);
                for (int i = 2; i < split.length; i+=2){
                    if (!FilesHandler.getTable(split[0].toLowerCase(), currentDatabase).containColumn(split[i].toLowerCase()))
                        throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
                }
                Object[] vals = new Object[split.length-2];
                for(int i = 2; i < split.length; i+= 2){
                    vals[i-2] = split[i];
                    vals[i-1] = Factory.getInstance().getObject(split[i+1]);
                }
                FilesHandler.getTable(split[0].toLowerCase(), currentDatabase).updateTable(vals);
                FilesHandler.saveTable(currentTable);
                return FilesHandler.getTable(split[0].toLowerCase(), currentDatabase).getIDCounter();
            }
            else{
                Pattern pattern = Pattern.compile("(?i)^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)");
                Matcher matcher = pattern.matcher(query);
                matcher.find();
                String[] split = matcher.group().replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
                query = query.toLowerCase().replaceAll("^\\w+\\s+set\\s+(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*,\\s*)*\\s*(\\w+\\s*=\\s*([0-9]+|\\'\\s*\\w+\\s*\\')\\s*)", "");
                String[] condition = parseQuery(query);
                if(!FilesHandler.isTableExist(split[0].toLowerCase(), currentDatabase))
                    throw new SQLException();
                for (int i = 2; i < split.length; i+=2){
                    if (!FilesHandler.getTable(split[0].toLowerCase(), currentDatabase).containColumn(split[i].toLowerCase()))
                        throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
                }
                Object[] vals = new Object[split.length-2];
                for(int i = 2; i < split.length; i+= 2){
                    vals[i-2] = split[i];
                    vals[i-1] = Factory.getInstance().getObject(split[i+1]);
                }
                Table table = aSwitch.meetCondition(condition[2], FilesHandler.getTable(split[0].toLowerCase(), currentDatabase), condition[1], Factory.getInstance().getObject(condition[3]));
                table.updateTable(vals);
                FilesHandler.getTable(split[0].toLowerCase(), currentDatabase).updateTable(table);
                FilesHandler.saveTable(currentTable);
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

    public Object[][] sortTable(Object[][] table, String input) throws SQLException {
        List<Integer> idx = new ArrayList<>();
        input = input.replaceAll("\\s+order\\s+by\\s+", "").replaceAll(",", " ");
        String[] split = input.split("\\s+");
        List<Column> temp = currentTable.getColumns();
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

    public String getCurrentDatabase() {
        return currentDatabase;
    }
    public void setCurrentDatabase(String database){
        currentDatabase = database;
    }
    public Table getCurrentTable(){
        return currentTable;
    }
    public void setCurrentTable(Table table){
        currentTable = table;
    }
}
