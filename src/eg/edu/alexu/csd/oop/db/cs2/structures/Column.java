package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Column{
    private String type;
    private String name;
    private List<Record> records;
    public Column(String n, String t){
        this.name = n;
        this.type = t;
        this.records = new ArrayList<>();
    }
    public String getType(){
        return type;
    }
    public String getName(){
        return name;
    }
    public List<Record> getRecords(){
        return records;
    }
    public void addRecord(Record record) throws SQLException {
            if(!Factory.getInstance().checkInstance(type, record.getValue()))
                throw new SQLException("Unable to add this record to with type" +  record.getType() + " to " +this.getName() + " with type = "+ this.getType());
            records.add(record);

    }
    public int getSize(){
        return records.size();
    }
    public Record getRecordAtIndex(int index){
        return records.get(index);
    }
    public void deleteRecord(int index){
        records.remove(index);
    }
    public int getIndexOfID(Record rec){
        int i = 0;
        for (Record record : records){
            if (rec.getValue().equals(record.getValue())){
                return i;
            }
            i++;
        }
        return -1;
    }
    public void updateAllRecords(Object value) throws SQLException {
            for (Record record : records){
                record.setValue(value);
            }
    }
    public void updateRecord(int index, Record newRecord) throws SQLException {
        this.records.get(index).setValue(newRecord.getValue());
    }
}
