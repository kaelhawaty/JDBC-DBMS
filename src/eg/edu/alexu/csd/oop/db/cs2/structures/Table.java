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

}
