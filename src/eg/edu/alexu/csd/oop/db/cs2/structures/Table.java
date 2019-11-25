package eg.edu.alexu.csd.oop.db.cs2.structures;


import java.sql.SQLException;
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
        columns.add(new Column(name, type));
    }
    public void addRow(HashMap<String, Object> values) throws SQLException {
        for (Column column : columns){
            if(values.containsKey(column.getName()) && !Factory.getInstance().checkInstance(column.getType(), values.get(column.getName()))) {
                    throw new SQLException("Cannot add this Object " + values.get(column.getName()) + " to column " + column.getName());
            }
        }
        for (Column column : columns){
            if(values.containsKey(column.getName())) {
                column.addRecord(new Record(values.get(column.getName()), column.getType()));
            }else {
                column.addRecord(null);
            }
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
    public void addRow(Object[] values) throws SQLException {
        int i = 0;
        columns.get(0).addRecord(new Record(IDCounter, "int"));
        for (int j = 1; j < columns.size(); ++j)
            columns.get(j).addRecord(new Record(values[i++], columns.get(j).getType()));
        this.IDCounter++;
    }
    public void addRow(List<Record> records) throws SQLException {
        int i = 0;
        for (Column column : columns)
            column.addRecord(new Record(records.get(i++).getValue(), column.getType()));
        this.IDCounter++;
    }
    public boolean containColumn(String columnName){
        for (Column column:columns){
            if(column.getName().equals(columnName))
                return true;
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
    public void updateTable(Object[] info) throws SQLException {
        for(int i = 0; i < info.length; i+=2){
            getColumn((String)info[i]).updateAllRecords(info[i+1]);
        }
    }
    public void updateTable(Table toUpdate) throws SQLException {
        List<Column> toUpdateColumns = toUpdate.getColumns();
        for(int i = 0; i < toUpdateColumns.get(0).getSize(); ++i)
            this.updateRow(columns.get(0).getIndexOfID(toUpdateColumns.get(0).getRecords().get(i)), toUpdate.getRow(i));
    }
    private void updateRow(int index, List<Record> values) throws SQLException {
        int i = 0;
        for (Column column : columns){
            if(!Factory.getInstance().checkInstance(column.getType(), values.get(i++).getValue())){
                throw new SQLException("Cannot add this Object " + values.get(i-1) + " to column " + column.getName());
            }
        }
        i = 0;
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
