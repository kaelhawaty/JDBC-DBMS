package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Table {
    private String name;
    private List<Column> columns;
    private int IDCounter;
    public Table(String name){
        this.name = name;
        this.columns = new ArrayList<>();
        this.addColumn("ID", "int");
        this.IDCounter = 0;
    }
    public Table(Table table){
        this.name = table.getName();
        List<Column> tableColumns = table.getColumns();
        this.columns = new ArrayList<>();
        for(Column column : tableColumns){
            this.addColumn(column.getName(), column.getType());
        }
        this.IDCounter = table.getIDCounter();
    }
    public int getIDCounter(){
        return IDCounter;
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
        this.IDCounter++;
    }
    public List<Record> getRow(int index){
        List<Record> record = new ArrayList<>();
        for (Column column : columns){
            record.add(column.getRecordAtIndex(index));
        }
        return record;
    }
    public void addRow(String[] values){
        int i = 2;
        columns.get(0).addRecord(new Record<Integer>(IDCounter));
        for (int j = 1; j < columns.size(); ++j){
            if (columns.get(j).getType().equalsIgnoreCase("int"))
                columns.get(j).addRecord(new Record<Integer>(Integer.parseInt(values[i++])));
            else
                columns.get(j).addRecord(new Record<String>((values[i++])));
        }
        this.IDCounter++;
    }
    public void addRow(List<Record> records){
        int i = 0;
        for (Column column : columns){
            column.addRecord(new Record(records.get(i++)));
        }
        this.IDCounter++;
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
    public int clear(){
        if(columns.size()==0)
            return 0;
        else
        {
            int size = columns.get(0).getSize();
            columns = new ArrayList<>();
            return size;
        }
    }
    public int deleteItems(Table toDelete){
        List<Column> toDeleteColumns = toDelete.getColumns();
        for(int i = 0; i < toDeleteColumns.get(0).getSize(); ++i)
            this.deleteRow(toDeleteColumns.get(0).getRecordAtIndex(i));
        return toDeleteColumns.get(0).getSize();
    }
    public void deleteRow(Record toDelete){
        int index = (Integer)toDelete.getValue();
        for (Column column : columns){
            column.deleteRecord(index);
        }
    }
}
