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
        if (!QueriesParser.checkExecuteQuery(query))
            throw new SQLException("Synatx Error");
       Pattern p = Pattern.compile("(?i)(\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?)");
        Matcher m = p.matcher(query);
        String order = null;
        if(m.find()){
            order = m.group();
            query = query.replaceAll("(?i)(\\s+order\\s+by\\s+(\\w+\\s*(\\s+(asc|desc))?\\s*,\\s*)*(\\w+\\s*(\\s+(asc|desc))?)(asc|desc)?)", "");
        }
        objects = new SelectTable().execute(query);
        if(order != null){
            objects = sortTable(objects, order);
        }
        return objects;
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        if(currentDatabase == null)
            return 0;
        return UpdateFactory.getInstance().create(query).execute(query);
    }

    public Object[][] sortTable(Object[][] table, String input) throws SQLException {
        List<Integer> idx = new ArrayList<>();
        input = input.replaceAll("\\s+order\\s+by\\s+", "").replaceAll(",", " ");
        String[] split = input.split("\\s+");
        List<Column> temp = currentTable.getColumns();
        for(int i = 0; i < split.length; i++){
            if(split[i].equalsIgnoreCase("desc")){
                idx.set(idx.size()-1 ,idx.get(idx.size()-1) *-1);
                continue;
            }else if(split[i].equalsIgnoreCase("asc")){
               continue;
            }
            int j;
            for(j = 1; j < temp.size(); j++){
                if(temp.get(j).getName().equalsIgnoreCase(split[i])){
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
