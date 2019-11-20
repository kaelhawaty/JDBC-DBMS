package eg.edu.alexu.csd.oop.db.cs2.structures;

public class Record <T>{
    private T value;
    public Record(T v){
        this.value = v;
    }
    public Record(Record record){
        this.value = (T) record.getValue();
    }
    public void setValue(T v){
        this.value = v;
    }
    public T getValue() {
        return value;
    }
}
