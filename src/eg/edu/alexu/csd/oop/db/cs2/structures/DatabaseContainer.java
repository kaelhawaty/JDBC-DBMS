package eg.edu.alexu.csd.oop.db.cs2.structures;


import javafx.scene.control.Tab;

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
    public void addTable(String[] tableInfo){
        Table tab = new Table(tableInfo[0]);
        for(int i = 1; i < tableInfo.length; i+=2){
            tab.addColumn(tableInfo[i], tableInfo[i+1]);
        }
        tables.add(tab);
    }
    public void removeTable(String tableName){
        for (Table table : tables){
            if (table.getName().equals(tableName))
                tables.remove(table);
        }
    }
    public void insertRow(String tableName, HashMap values){
        for (Table table : tables){
            if (table.getName().equals(tableName))
                table.addRow(values);
        }
    }
}
