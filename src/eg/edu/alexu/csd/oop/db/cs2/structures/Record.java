package eg.edu.alexu.csd.oop.db.cs2.structures;

import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;

import java.sql.SQLException;

public class Record {
    private Object value;
    private String type;
    public Record(Object v, String type){
        this.value = v;
        this.type = type;
    }
    public Record(Record record){
        this.value = record.getValue();
        this.type = record.getType();
    }
    public void setValue(Object v) throws SQLException {
        if(!Factory.getInstance().checkInstance(type, v)){
            throw new SQLException("Cannot store this " +  v  + " in this record " + this + " of type " + type);
        }
        this.value = v;
    }
    public String getType(){
        return type;
    }
    public Object getValue() {
        return value;
    }
}
