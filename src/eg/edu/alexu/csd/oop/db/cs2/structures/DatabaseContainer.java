/*package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseContainer {
    private String name;
    private List<Table> tables;
    public DatabaseContainer(String n){
        this.name = n;
        tables = new ArrayList<>();
    }
    public String getName(){
        return name;
    }
    public Table getTable(String tableName){
        for (Table table:tables){
            if(table.getName().equals(tableName))
                return table;
        }
        return null;
    }
    public void addTable(String[] tableInfo){
        Table tab = new Table(tableInfo[0]);
        for(int i = 1; i < tableInfo.length; i+=2){
            tab.addColumn(tableInfo[i], tableInfo[i+1]);
        }
        tables.add(tab);
    }
    public void addTable(Table table){
        tables.add(table);
    }
    public void removeTable(String tableName){
        Table table = getTable(tableName);
        tables.remove(table);
    }
    public void insertRow(String tableName, HashMap<String, Object> values) throws SQLException {
        Table table = getTable(tableName);
        table.addRow(values);
    }
    public void insertRow(String tableName, Object[] values) throws SQLException {
        Table table = getTable(tableName);
        table.addRow(values);
    }
    public boolean containColumn(String tableName, String columnName){
        Table table = getTable(tableName);
        return table.containColumn(columnName);
    }
    public int getTableNumOfColumns(String tableName){
        Table table = getTable(tableName);
        return table.getSize();
    }
    public int clearTable(String tableName){
        Table table = getTable(tableName);
        return table.clear();
    }
    public int deleteItems(String tableName, Table toDelete){
        Table curTable = getTable(tableName);
        return curTable.deleteItems(toDelete);
    }
}
*/