package eg.edu.alexu.csd.oop.db.cs2.structures;

public class Record <T>{
    private T value;
    public Record(T v){
        this.value = v;
    }
    public void setValue(T v){
        this.value = v;
    }

    public T getValue() {
        return value;
    }
    @Override
    public String toString(){
        return ""+this;
    }
}
