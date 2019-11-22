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
        this.IDCounter = 0;
    }
    public int getIDCounter(){
        return IDCounter;
    }
    public String getName(){
        return name;
    }
    public void addColumn(String name, String type){
        Column column;
        if (type.equalsIgnoreCase("int"))
            column = new Column<Integer>(name, type);
        else
            column = new Column<String>(name, type);
        columns.add(column);
    }
    public void addRow(HashMap values){
        for (Column column : columns){
            if(values.containsKey(column.getName())){
                if (column.getName().equalsIgnoreCase("int"))
                    column.addRecord(new Record<>(Integer.parseInt((String) values.get(column.getName()))));
                else
                    column.addRecord(new Record<>(values.get(column.getName())));
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
        columns.get(0).addRecord(new Record<>(IDCounter));
        for (int j = 1; j < columns.size(); ++j){
            if (columns.get(j).getType().equalsIgnoreCase("int"))
                columns.get(j).addRecord(new Record<>(Integer.parseInt((values[i++]))));
            else
                columns.get(j).addRecord(new Record(values[i++]));
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
    public List<Column> getColumns(String[] columnsName){
        List<Column> matched = new ArrayList<>();
        for (String columnName : columnsName){
            for (Column column : columns){
                if (columnName.equals(column.getName())){
                    matched.add(column);
                }
            }
        }
        return matched;
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
            this.deleteRow(this.getColumns().get(0).getIndexOfID((Record) (toDeleteColumns.get(0).getRecords().get(i))));
        return toDeleteColumns.get(0).getSize();
    }
    private void deleteRow(int index){
        for (Column column : columns){
            column.deleteRecord(index);
        }
    }
    public void updateTable(String[] info){
        for(int i = 2; i < info.length; i+=2){
            getColumn(info[i]).updateAllRecords(info[i+1]);
        }
    }
    public void updateTable(Table toUpdate){
        List<Column> toUpdateColumns = toUpdate.getColumns();
        for(int i = 0; i < toUpdateColumns.get(0).getSize(); ++i)
            this.updateRow(columns.get(0).getIndexOfID((Record)toUpdateColumns.get(0).getRecords().get(i)), toUpdate.getRow(i));
    }
    private void updateRow(int index, List<Record> values){
        int i = 0;
        for (Column column : columns){
            column.updateRecord(index, values.get(i++));
        }
    }
    private Column getColumn(String columnName){
        for (Column column : columns){
            if (column.getName().equalsIgnoreCase(columnName))
                return column;
        }
        return null;
    }
}
