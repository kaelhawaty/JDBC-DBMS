package eg.edu.alexu.csd.oop.db.cs2.structures;


import java.util.ArrayList;
import java.util.List;

public class DatabaseContainer {
    private String name;
    private List<Table> tables;
    public DatabaseContainer(String n){
        this.name = n;
        tables = new ArrayList<>();
    }
    public String getName(){
        return name;
    }
    public void addTable(String[] tableInfo){

    }
    public void removeTable(String tableName){

    }
}
