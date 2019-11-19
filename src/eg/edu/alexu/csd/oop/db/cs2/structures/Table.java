package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table {
    private String name;
    private List<Column> columns;
    public Table(String name){
        this.name = name;
        this.columns = new ArrayList<>();
    }
    public String getName(){
        return name;
    }
    public void addColumn(String name, String type){
        Column column;
        if (type.equalsIgnoreCase("int")) {
            column = new Column<Integer>(name, type);
        }else {
            column = new Column<String>(name, type);
        }
        columns.add(column);
    }
    public void addColumn(Column col){
        columns.add(col);
    }
    public void addRow(HashMap values){
        for (Column column : columns){
            if(values.containsKey(column.getName())){
                if (column.getType().equalsIgnoreCase("int"))
                    column.addRecord(new Record<Integer>(Integer.parseInt((String) values.get(column.getName()))));
                else
                    column.addRecord(new Record<String>((String) values.get(column.getName())));
            }
            else
                column.addRecord(null);
        }
    }
    public void addRow(String[] values){
        int i = 2;
        for (Column column : columns){
            if (column.getType().equalsIgnoreCase("int"))
                column.addRecord(new Record<Integer>(Integer.parseInt(values[i++])));
            else
                column.addRecord(new Record<String>((values[i++])));
        }
    }
    public boolean containColumn(String columnName){
        for (Column column:columns){
            if(column.getName().equals(columnName)){
                return true;
            }
        }
        return false;
    }
    public int getSize(){
        return columns.size();
    }
    public List<Column> getColumns(){
        return columns;
    }
}
