package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.util.ArrayList;
import java.util.List;

public class Column <T>{
    private String type;
    private String name;
    private List<Record<T>> records;
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
    public List<Record<T>> getRecords(){
        return records;
    }
    public void addRecord(Record record){
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
}
